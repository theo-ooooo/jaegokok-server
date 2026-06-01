package com.jaegokok.domain.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Product(
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
        String imageUrl,
        LocalDateTime createdAt
) {}
