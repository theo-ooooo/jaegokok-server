package com.jaegokok.domain.inventory.dto;

import com.jaegokok.core.inventory.InventoryType;

public record ScanResponse(
        Long productId,
        String productName,
        int previousStock,
        int currentStock,
        InventoryType type,
        int quantity,
        boolean lowStock
) {}
