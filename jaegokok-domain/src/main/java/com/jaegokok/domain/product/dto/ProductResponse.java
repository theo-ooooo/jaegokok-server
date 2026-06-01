package com.jaegokok.domain.product.dto;

import com.jaegokok.domain.product.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        String qrCode,
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
                product.qrCode(),
                product.createdAt()
        );
    }
}
