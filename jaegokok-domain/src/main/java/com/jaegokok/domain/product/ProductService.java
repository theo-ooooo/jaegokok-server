package com.jaegokok.domain.product;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.common.util.Filenames;
import com.jaegokok.core.image.ImageEntityType;
import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.file.FileUploadPort;
import com.jaegokok.domain.file.ImageEncoderPort;
import com.jaegokok.domain.image.ImageRepository;
import com.jaegokok.domain.product.dto.CreateProductRequest;
import com.jaegokok.domain.product.dto.ProductResponse;
import com.jaegokok.domain.product.dto.ProductSearchCondition;
import com.jaegokok.domain.product.dto.UpdateProductRequest;
import com.jaegokok.domain.subscription.SubscriptionPlanRepository;
import com.jaegokok.domain.workspace.Workspace;
import com.jaegokok.domain.workspace.WorkspaceMemberRepository;
import com.jaegokok.domain.workspace.WorkspaceRepository;
import com.jaegokok.domain.workspace.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceService workspaceService;
    private final QrCodePort qrCodePort;
    private final FileUploadPort fileUploadPort;
    private final ImageEncoderPort imageEncoderPort;
    private final ImageRepository imageRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Transactional
    public ProductResponse createInWorkspace(Long workspaceId, CreateProductRequest request) {
        WorkspacePlan effectivePlan = workspaceService.getEffectivePlan(workspaceId);
        int limit = subscriptionPlanRepository.findByPlanKey(effectivePlan.name())
                .map(sp -> sp.isUnlimitedProducts() ? Integer.MAX_VALUE : sp.productLimit())
                .orElse(Integer.MAX_VALUE);
        if (productRepository.countByWorkspaceId(workspaceId) >= limit) {
            throw new CustomException(ErrorCode.PRODUCT_LIMIT_EXCEEDED);
        }
        String qrCode = UUID.randomUUID().toString();
        return ProductResponse.from(productRepository.save(workspaceId, request, qrCode), fileUploadPort);
    }

    @Transactional
    public ProductResponse create(Long memberId, CreateProductRequest request) {
        Workspace workspace = getOwnerWorkspace(memberId);
        WorkspacePlan effectivePlan = workspaceService.getEffectivePlan(workspace.id());
        int limit = subscriptionPlanRepository.findByPlanKey(effectivePlan.name())
                .map(sp -> sp.isUnlimitedProducts() ? Integer.MAX_VALUE : sp.productLimit())
                .orElse(Integer.MAX_VALUE);
        if (productRepository.countByWorkspaceId(workspace.id()) >= limit) {
            throw new CustomException(ErrorCode.PRODUCT_LIMIT_EXCEEDED);
        }
        String qrCode = UUID.randomUUID().toString();
        return ProductResponse.from(productRepository.save(workspace.id(), request, qrCode), fileUploadPort);
    }

    public Page<ProductResponse> findAllInWorkspace(Long workspaceId, ProductSearchCondition condition, Pageable pageable) {
        return productRepository.findByWorkspaceId(workspaceId, condition, pageable)
                .map(p -> ProductResponse.from(p, fileUploadPort));
    }

    public Page<ProductResponse> findAll(Long memberId, ProductSearchCondition condition, Pageable pageable) {
        Workspace workspace = getMemberWorkspace(memberId);
        return productRepository.findByWorkspaceId(workspace.id(), condition, pageable)
                .map(p -> ProductResponse.from(p, fileUploadPort));
    }

    public ProductResponse findByIdInWorkspace(Long workspaceId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.workspaceId().equals(workspaceId)) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        return ProductResponse.from(product, fileUploadPort);
    }

    public ProductResponse findById(Long memberId, Long productId) {
        Workspace workspace = getMemberWorkspace(memberId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.workspaceId().equals(workspace.id())) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        return ProductResponse.from(product, fileUploadPort);
    }

    @Transactional
    public ProductResponse update(Long memberId, Long productId, UpdateProductRequest request) {
        Workspace workspace = getOwnerWorkspace(memberId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.workspaceId().equals(workspace.id())) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        return ProductResponse.from(productRepository.update(productId, request), fileUploadPort);
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
        Workspace workspace = getMemberWorkspace(memberId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.workspaceId().equals(workspace.id())) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        return qrCodePort.generateQrPng(workspace.slug(), product.qrCode());
    }

    public byte[] downloadBulkQrPdf(Long memberId, List<Long> productIds) {
        Workspace workspace = getMemberWorkspace(memberId);
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
                .map(p -> new QrItem(workspace.slug(), p.qrCode(), p.name()))
                .toList();
        return qrCodePort.generateBulkQrPdf(items);
    }

    @Transactional
    public ProductResponse uploadImageInWorkspace(Long workspaceId, Long productId, String originalFilename, byte[] content, String contentType) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.workspaceId().equals(workspaceId)) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        return doUploadImage(productId, originalFilename, content, contentType);
    }

    @Transactional
    public ProductResponse uploadImage(Long memberId, Long productId, String originalFilename, byte[] content, String contentType) {
        Workspace workspace = getOwnerWorkspace(memberId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.workspaceId().equals(workspace.id())) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        return doUploadImage(productId, originalFilename, content, contentType);
    }

    private ProductResponse doUploadImage(Long productId, String originalFilename, byte[] content, String contentType) {
        String originalKey = fileUploadPort.upload("products/" + productId + "/original", originalFilename, content, contentType);
        String webpKey = tryConvertAndUploadWebp("products/" + productId + "/webp", originalFilename, content);
        imageRepository.deleteByEntity(ImageEntityType.PRODUCT, productId);
        imageRepository.save(ImageEntityType.PRODUCT, productId, originalKey, webpKey, fileUploadPort.getBucket());
        return ProductResponse.from(productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)), fileUploadPort);
    }

    private String tryConvertAndUploadWebp(String directory, String originalFilename, byte[] content) {
        try {
            byte[] webpBytes = imageEncoderPort.toWebp(content);
            return fileUploadPort.upload(directory, Filenames.stripExtension(originalFilename) + ".webp", webpBytes, "image/webp");
        } catch (RuntimeException e) {
            log.warn("WebP conversion/upload failed, falling back to original-only", e);
            return null;
        }
    }

    private Workspace getOwnerWorkspace(Long memberId) {
        return workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
    }

    private Workspace getMemberWorkspace(Long memberId) {
        return workspaceRepository.findByOwnerId(memberId)
                .or(() -> workspaceMemberRepository.findByMemberId(memberId)
                        .flatMap(wm -> workspaceRepository.findById(wm.workspaceId())))
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
    }

    private Workspace getMemberWorkspaceBySlug(Long memberId, String workspaceSlug) {
        Workspace workspace = workspaceRepository.findBySlug(workspaceSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        if (!workspaceMemberRepository.existsByWorkspaceIdAndMemberId(workspace.id(), memberId)) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        return workspace;
    }
}
