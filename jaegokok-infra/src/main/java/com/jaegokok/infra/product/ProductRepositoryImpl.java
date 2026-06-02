package com.jaegokok.infra.product;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.image.ImageEntityType;
import com.jaegokok.core.product.ProductEntity;
import com.jaegokok.domain.image.Image;
import com.jaegokok.domain.product.Product;
import com.jaegokok.domain.product.ProductRepository;
import com.jaegokok.domain.product.dto.CreateProductRequest;
import com.jaegokok.domain.product.dto.ProductSearchCondition;
import com.jaegokok.domain.product.dto.UpdateProductRequest;
import com.jaegokok.infra.image.ImageJpaRepository;
import com.jaegokok.infra.workspace.WorkspaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductQueryRepository productQueryRepository;
    private final WorkspaceJpaRepository workspaceJpaRepository;
    private final ImageJpaRepository imageJpaRepository;

    @Override
    public Product save(Long workspaceId, CreateProductRequest request, String qrCode) {
        ProductEntity entity = ProductEntity.from(
                workspaceJpaRepository.getReferenceById(workspaceId),
                request.name(),
                request.sku(),
                request.description(),
                request.price(),
                request.unit(),
                request.category(),
                request.minStockLevel() != null ? request.minStockLevel() : 0,
                0,
                qrCode
        );
        return toProduct(productJpaRepository.save(entity));
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id).map(this::toProduct);
    }

    @Override
    public Optional<Product> findByQrCode(String qrCode) {
        return productJpaRepository.findByQrCode(qrCode).map(this::toProduct);
    }

    @Override
    public List<Product> findAllByIds(List<Long> ids) {
        return productJpaRepository.findAllByIdIn(ids).stream()
                .map(this::toProduct)
                .toList();
    }

    @Override
    public Page<Product> findByWorkspaceId(Long workspaceId, ProductSearchCondition condition, Pageable pageable) {
        return productQueryRepository.findByWorkspaceId(workspaceId, condition, pageable)
                .map(this::toProduct);
    }

    @Override
    public long countByWorkspaceId(Long workspaceId) {
        return productJpaRepository.countByWorkspace_Id(workspaceId);
    }

    @Override
    public Product update(Long productId, UpdateProductRequest request) {
        ProductEntity entity = productJpaRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        entity.update(
                request.name(),
                request.sku(),
                request.description(),
                request.price(),
                request.unit(),
                request.category(),
                request.minStockLevel()
        );
        return toProduct(entity);
    }

    @Override
    public void deleteById(Long id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void adjustStock(Long productId, int delta) {
        productJpaRepository.adjustStock(productId, delta);
    }

    @Override
    @Transactional
    public int adjustStockOut(Long productId, int quantity) {
        return productJpaRepository.adjustStockOut(productId, quantity);
    }

    private Product toProduct(ProductEntity e) {
        List<Image> images = imageJpaRepository.findByEntityTypeAndEntityId(ImageEntityType.PRODUCT, e.getId())
                .stream().map(img -> new Image(img.getId(), img.getEntityType(), img.getEntityId(), img.getOriginalPath(), img.getWebpPath(), img.getBucket(), img.getCreatedAt()))
                .toList();
        return new Product(e.getId(), e.getWorkspace().getId(), e.getName(), e.getSku(), e.getDescription(),
                e.getPrice(), e.getUnit(), e.getCategory(), e.getMinStockLevel(), e.getCurrentStock(), e.getQrCode(), images, e.getCreatedAt());
    }
}
