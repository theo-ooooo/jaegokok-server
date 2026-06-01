package com.jaegokok.domain.inventory.dto;

import com.jaegokok.core.inventory.InventoryType;
import java.time.LocalDate;

public record InventoryHistoryCondition(
        Long productId,
        InventoryType type,
        LocalDate dateFrom,
        LocalDate dateTo
) {}
