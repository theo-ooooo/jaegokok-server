package com.jaegokok.domain.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 100) String sku,
        @Size(max = 1000) String description,
        @DecimalMin("0") BigDecimal price,
        @Size(max = 50) String unit,
        @Size(max = 100) String category,
        @Min(0) Integer minStockLevel
) {}
