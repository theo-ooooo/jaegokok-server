package com.jaegokok.domain.inventory.dto;

import com.jaegokok.domain.inventory.InventoryRecord;

import java.util.List;

public record StockResponse(
        Long productId,
        String productName,
        int currentStock,
        int minStockLevel,
        boolean lowStock,
        List<InventoryRecord> recentRecords
) {}
