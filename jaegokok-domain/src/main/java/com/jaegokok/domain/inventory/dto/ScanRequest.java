package com.jaegokok.domain.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ScanRequest(
        @NotNull @Min(1) Integer quantity,
        String note
) {}
