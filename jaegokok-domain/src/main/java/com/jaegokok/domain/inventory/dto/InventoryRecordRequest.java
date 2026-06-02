package com.jaegokok.domain.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryRecordRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity,
        String note
) {}
