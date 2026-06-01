package com.jaegokok.domain.product;

import java.util.List;

public interface ProductImageRepository {
    ProductImage save(Long productId, String originalPath, String webpPath, String bucket);
    List<ProductImage> findByProductId(Long productId);
    void deleteByProductId(Long productId);
}
