package com.jaegokok.infra.product;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.product.ProductEntity;
import com.jaegokok.domain.product.Product;
import com.jaegokok.domain.product.ProductRepository;
import com.jaegokok.domain.product.dto.CreateProductRequest;
import com.jaegokok.domain.product.dto.ProductSearchCondition;
import com.jaegokok.domain.product.dto.UpdateProductRequest;
import com.jaegokok.infra.workspace.WorkspaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductQueryRepository productQueryRepository;
    private final WorkspaceJpaRepository workspaceJpaRepository;

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
                qrCode
        );
        return toProduct(productJpaRepository.save(entity));
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id).map(this::toProduct);
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

    private Product toProduct(ProductEntity e) {
        return new Product(
                e.getId(),
                e.getWorkspace().getId(),
                e.getName(),
                e.getSku(),
                e.getDescription(),
                e.getPrice(),
                e.getUnit(),
                e.getCategory(),
                e.getMinStockLevel(),
                e.getQrCode(),
                e.getCreatedAt()
        );
    }
}
