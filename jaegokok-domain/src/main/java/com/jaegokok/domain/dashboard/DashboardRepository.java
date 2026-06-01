package com.jaegokok.domain.dashboard;

import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.domain.dashboard.dto.LowStockProduct;

import java.time.LocalDate;
import java.util.List;

public interface DashboardRepository {
    long countProducts(Long workspaceId);
    List<LowStockProduct> findLowStockProducts(Long workspaceId);
    long countTodayRecords(Long workspaceId, InventoryType type, LocalDate today);
}
