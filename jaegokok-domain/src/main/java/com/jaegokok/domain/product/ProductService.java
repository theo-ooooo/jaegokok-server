package com.jaegokok.domain.product;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.image.ImageEntityType;
import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.file.FileUploadPort;
import com.jaegokok.domain.image.ImageRepository;
import com.jaegokok.domain.product.dto.CreateProductRequest;
import com.jaegokok.domain.product.dto.ProductResponse;
import com.jaegokok.domain.product.dto.ProductSearchCondition;
import com.jaegokok.domain.product.dto.UpdateProductRequest;
import com.jaegokok.domain.workspace.Workspace;
import com.jaegokok.domain.workspace.WorkspaceRepository;
import com.jaegokok.domain.workspace.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private static final Map<WorkspacePlan, Integer> PLAN_LIMITS = Map.of(
            WorkspacePlan.FREE, 50,
            WorkspacePlan.BASIC, 500,
            WorkspacePlan.PRO, Integer.MAX_VALUE
    );

    private final ProductRepository productRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceService workspaceService;
    private final QrCodePort qrCodePort;
    private final FileUploadPort fileUploadPort;
    private final ImageRepository imageRepository;

    @Transactional
    public ProductResponse create(Long memberId, CreateProductRequest request) {
        Workspace workspace = getOwnerWorkspace(memberId);
        WorkspacePlan effectivePlan = workspaceService.getEffectivePlan(workspace.id());
        int limit = PLAN_LIMITS.getOrDefault(effectivePlan, Integer.MAX_VALUE);
        if (productRepository.countByWorkspaceId(workspace.id()) >= limit) {
            throw new CustomException(ErrorCode.PRODUCT_LIMIT_EXCEEDED);
        }
        String qrCode = UUID.randomUUID().toString();
        return ProductResponse.from(productRepository.save(workspace.id(), request, qrCode));
    }

    public Page<ProductResponse> findAll(Long memberId, ProductSearchCondition condition, Pageable pageable) {
        Workspace workspace = getOwnerWorkspace(memberId);
        return productRepository.findByWorkspaceId(workspace.id(), condition, pageable)
                .map(ProductResponse::from);
    }

    public ProductResponse findById(Long memberId, Long productId) {
        Workspace workspace = getOwnerWorkspace(memberId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.workspaceId().equals(workspace.id())) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse update(Long memberId, Long productId, UpdateProductRequest request) {
        Workspace workspace = getOwnerWorkspace(memberId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.workspaceId().equals(workspace.id())) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        return ProductResponse.from(productRepository.update(productId, request));
    }

    @Transactional
    public void delete(Long memberId, Long productId) {
        Workspace workspace = getOwnerWorkspace(memberId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.workspaceId().equals(workspace.id())) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        productRepository.deleteById(productId);
    }

    public byte[] downloadQrPng(Long memberId, Long productId) {
        Workspace workspace = getOwnerWorkspace(memberId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.workspaceId().equals(workspace.id())) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        return qrCodePort.generateQrPng(product.qrCode());
    }

    public byte[] downloadBulkQrPdf(Long memberId, List<Long> productIds) {
        Workspace workspace = getOwnerWorkspace(memberId);
        List<Product> products = productRepository.findAllByIds(productIds);
        if (products.size() != productIds.stream().distinct().count()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        products.forEach(product -> {
            if (!product.workspaceId().equals(workspace.id())) {
                throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
            }
        });
        List<QrItem> items = products.stream()
                .map(p -> new QrItem(p.qrCode(), p.name()))
                .toList();
        return qrCodePort.generateBulkQrPdf(items);
    }

    @Transactional
    public ProductResponse uploadImage(Long memberId, Long productId, String originalFilename, byte[] content, String contentType) {
        Workspace workspace = getOwnerWorkspace(memberId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.workspaceId().equals(workspace.id())) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        String originalPath = fileUploadPort.upload("products/" + productId + "/original", originalFilename, content, contentType);
        imageRepository.deleteByEntity(ImageEntityType.PRODUCT, productId);
        imageRepository.save(ImageEntityType.PRODUCT, productId, originalPath, null, fileUploadPort.getBucket());
        return ProductResponse.from(productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)));
    }

    private Workspace getOwnerWorkspace(Long memberId) {
        return workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
    }
}
