package com.jaegokok.domain.dashboard.dto;

public record LowStockProduct(
        Long id,
        String name,
        int currentStock,
        int minStockLevel,
        String unit
) {}
