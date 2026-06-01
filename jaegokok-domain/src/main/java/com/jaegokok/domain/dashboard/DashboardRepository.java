package com.jaegokok.domain.dashboard;

import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.domain.dashboard.dto.LowStockProduct;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DashboardRepository {
    long countProducts(Long workspaceId);
    long countLowStockProducts(Long workspaceId);
    List<LowStockProduct> findLowStockProducts(Long workspaceId, int limit);
    Map<InventoryType, Long> countTodayRecordsByType(Long workspaceId, LocalDate today);
}
