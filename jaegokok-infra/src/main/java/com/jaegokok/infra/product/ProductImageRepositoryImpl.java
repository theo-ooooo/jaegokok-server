package com.jaegokok.infra.product;

import com.jaegokok.core.product.ProductImageEntity;
import com.jaegokok.domain.product.ProductImage;
import com.jaegokok.domain.product.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductImageRepositoryImpl implements ProductImageRepository {

    private final ProductImageJpaRepository productImageJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    @Override
    public ProductImage save(Long productId, String originalPath, String webpPath, String bucket) {
        return toProductImage(productImageJpaRepository.save(
                ProductImageEntity.of(productJpaRepository.getReferenceById(productId), originalPath, webpPath, bucket)));
    }

    @Override
    public List<ProductImage> findByProductId(Long productId) {
        return productImageJpaRepository.findByProduct_Id(productId).stream()
                .map(this::toProductImage)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByProductId(Long productId) {
        productImageJpaRepository.deleteByProduct_Id(productId);
    }

    private ProductImage toProductImage(ProductImageEntity e) {
        return new ProductImage(e.getId(), e.getProduct().getId(), e.getOriginalPath(), e.getWebpPath(), e.getBucket(), e.getCreatedAt());
    }
}
