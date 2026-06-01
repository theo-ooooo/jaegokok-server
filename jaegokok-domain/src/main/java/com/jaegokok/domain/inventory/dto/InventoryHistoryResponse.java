package com.jaegokok.domain.inventory.dto;

import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.domain.inventory.InventoryRecord;

import java.time.LocalDateTime;

public record InventoryHistoryResponse(
        Long id,
        Long productId,
        String productName,
        InventoryType type,
        int quantity,
        String note,
        Long createdById,
        String createdByNickname,
        LocalDateTime createdAt
) {
    public static InventoryHistoryResponse from(InventoryRecord r) {
        return new InventoryHistoryResponse(r.id(), r.productId(), r.productName(), r.type(),
                r.quantity(), r.note(), r.createdById(), r.createdByNickname(), r.createdAt());
    }
}
