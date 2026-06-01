package com.jaegokok.domain.product;

import com.jaegokok.domain.image.Image;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
        List<Image> images,
        LocalDateTime createdAt
) {}
