package com.jaegokok.domain.product.dto;

import com.jaegokok.domain.product.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        Long workspaceId,
        String name,
        String sku,
        String description,
        BigDecimal price,
        String unit,
        String category,
        int minStockLevel,
        int currentStock,
        String qrCode,
        List<ProductImageResponse> images,
        LocalDateTime createdAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.id(),
                product.workspaceId(),
                product.name(),
                product.sku(),
                product.description(),
                product.price(),
                product.unit(),
                product.category(),
                product.minStockLevel(),
                product.currentStock(),
                product.qrCode(),
                product.images().stream().map(ProductImageResponse::from).toList(),
                product.createdAt()
        );
    }
}
