package com.jaegokok.domain.product.dto;

public record ProductSearchCondition(
        String name,
        String category,
        Boolean lowStock
) {}
