package com.jaegokok.domain.inventory;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.domain.file.FileUploadPort;
import com.jaegokok.domain.inventory.dto.PublicScanResponse;
import com.jaegokok.domain.inventory.dto.ScanRequest;
import com.jaegokok.domain.inventory.dto.ScanResponse;
import com.jaegokok.domain.inventory.dto.StockResponse;
import com.jaegokok.domain.product.Product;
import com.jaegokok.domain.product.ProductRepository;
import com.jaegokok.domain.workspace.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScanService {

    private final ProductRepository productRepository;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final FileUploadPort fileUploadPort;

    @Transactional
    public ScanResponse scanIn(String qrCode, Long memberId, ScanRequest request) {
        Product product = findByQrCodeOrThrow(qrCode);
        checkWorkspaceMember(product.workspaceId(), memberId);

        int previousStock = product.currentStock();
        productRepository.adjustStock(product.id(), request.quantity());
        int newStock = previousStock + request.quantity();
        inventoryRecordRepository.save(product.id(), InventoryType.IN, request.quantity(), request.note(), memberId);

        return new ScanResponse(product.id(), product.name(), previousStock, newStock,
                InventoryType.IN, request.quantity(), newStock <= product.minStockLevel());
    }

    @Transactional
    public ScanResponse scanOut(String qrCode, Long memberId, ScanRequest request) {
        Product product = findByQrCodeOrThrow(qrCode);
        checkWorkspaceMember(product.workspaceId(), memberId);

        int previousStock = product.currentStock();
        if (previousStock < request.quantity()) {
            throw new CustomException(ErrorCode.INSUFFICIENT_STOCK);
        }
        productRepository.adjustStock(product.id(), -request.quantity());
        int newStock = previousStock - request.quantity();
        inventoryRecordRepository.save(product.id(), InventoryType.OUT, request.quantity(), request.note(), memberId);

        return new ScanResponse(product.id(), product.name(), previousStock, newStock,
                InventoryType.OUT, request.quantity(), newStock <= product.minStockLevel());
    }

    public StockResponse getStock(Long productId, Long memberId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        checkWorkspaceMember(product.workspaceId(), memberId);
        List<InventoryRecord> recent = inventoryRecordRepository.findRecentByProductId(productId, 5);
        return new StockResponse(product.id(), product.name(), product.currentStock(),
                product.minStockLevel(), product.currentStock() <= product.minStockLevel(), recent);
    }

    public PublicScanResponse getProductByQrCode(String qrCode) {
        Product product = findByQrCodeOrThrow(qrCode);
        String imageUrl = (product.images() != null && !product.images().isEmpty())
                ? fileUploadPort.toUrl(product.images().get(0).originalPath())
                : null;
        return new PublicScanResponse(product.id(), product.name(), product.qrCode(), product.workspaceId(),
                product.currentStock(), imageUrl);
    }

    private Product findByQrCodeOrThrow(String qrCode) {
        return productRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private void checkWorkspaceMember(Long workspaceId, Long memberId) {
        if (!workspaceMemberRepository.existsByWorkspaceIdAndMemberId(workspaceId, memberId)) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
    }
}
