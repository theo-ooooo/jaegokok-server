package com.jaegokok.infra.inventory;

import com.jaegokok.core.inventory.InventoryRecordEntity;
import com.jaegokok.domain.inventory.dto.InventoryHistoryCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryRecordQueryRepository {
    List<InventoryRecordEntity> findRecentByProductId(Long productId, int limit);
    Page<InventoryRecordEntity> findByCondition(Long workspaceId, InventoryHistoryCondition condition, Pageable pageable);
}
