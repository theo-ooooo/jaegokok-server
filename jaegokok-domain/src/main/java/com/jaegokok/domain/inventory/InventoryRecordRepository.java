package com.jaegokok.domain.inventory;

import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.domain.inventory.dto.InventoryHistoryCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryRecordRepository {
    InventoryRecord save(Long productId, InventoryType type, int quantity, String note, Long createdById);
    List<InventoryRecord> findRecentByProductId(Long productId, int limit);
    Page<InventoryRecord> findByCondition(Long workspaceId, InventoryHistoryCondition condition, Pageable pageable);
}
