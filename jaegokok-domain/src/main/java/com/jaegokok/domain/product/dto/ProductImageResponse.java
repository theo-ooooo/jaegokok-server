package com.jaegokok.domain.product.dto;

import com.jaegokok.domain.product.ProductImage;

public record ProductImageResponse(
        Long id,
        String originalPath,
        String webpPath
) {
    public static ProductImageResponse from(ProductImage image) {
        return new ProductImageResponse(image.id(), image.originalPath(), image.webpPath());
    }
}
