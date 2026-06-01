package com.jaegokok.domain.inventory;

import com.jaegokok.core.inventory.InventoryType;
import java.time.LocalDateTime;

public record InventoryRecord(
        Long id,
        Long productId,
        String productName,
        Long workspaceId,
        InventoryType type,
        int quantity,
        String note,
        Long createdById,
        String createdByNickname,
        LocalDateTime createdAt
) {}
