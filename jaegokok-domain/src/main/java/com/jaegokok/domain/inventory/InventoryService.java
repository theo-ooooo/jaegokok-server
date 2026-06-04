package com.jaegokok.domain.inventory;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.domain.inventory.dto.InventoryHistoryCondition;
import com.jaegokok.domain.inventory.dto.InventoryHistoryResponse;
import com.jaegokok.domain.inventory.dto.InventoryRecordRequest;
import com.jaegokok.domain.product.Product;
import com.jaegokok.domain.product.ProductRepository;
import com.jaegokok.domain.workspace.WorkspaceMemberRepository;
import com.jaegokok.domain.workspace.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRecordRepository inventoryRecordRepository;
    private final ProductRepository productRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;

    public Page<InventoryHistoryResponse> getHistory(Long memberId, Long workspaceId, InventoryHistoryCondition condition, Pageable pageable) {
        if (!workspaceMemberRepository.existsByWorkspaceIdAndMemberId(workspaceId, memberId)) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        return inventoryRecordRepository.findByCondition(workspaceId, condition, pageable)
                .map(InventoryHistoryResponse::from);
    }

    public Page<InventoryHistoryResponse> getHistory(Long memberId, InventoryHistoryCondition condition, Pageable pageable) {
        Long workspaceId = workspaceMemberRepository.findAllByMemberId(memberId)
                .stream().findFirst()
                .map(wm -> wm.workspaceId())
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        return inventoryRecordRepository.findByCondition(workspaceId, condition, pageable)
                .map(InventoryHistoryResponse::from);
    }

    @Transactional
    public InventoryHistoryResponse recordIn(Long memberId, InventoryRecordRequest request) {
        Product product = findProductAndCheckAccess(memberId, request.productId());
        productRepository.adjustStock(product.id(), request.quantity());
        InventoryRecord record = inventoryRecordRepository.save(
                product.id(), InventoryType.IN, request.quantity(), request.note(), memberId);
        return InventoryHistoryResponse.from(record);
    }

    @Transactional
    public InventoryHistoryResponse recordOut(Long memberId, InventoryRecordRequest request) {
        Product product = findProductAndCheckAccess(memberId, request.productId());
        int updated = productRepository.adjustStockOut(product.id(), request.quantity());
        if (updated == 0) {
            throw new CustomException(ErrorCode.INSUFFICIENT_STOCK);
        }
        InventoryRecord record = inventoryRecordRepository.save(
                product.id(), InventoryType.OUT, request.quantity(), request.note(), memberId);
        return InventoryHistoryResponse.from(record);
    }

    private Product findProductAndCheckAccess(Long memberId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!workspaceMemberRepository.existsByWorkspaceIdAndMemberId(product.workspaceId(), memberId)) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        return product;
    }
}
