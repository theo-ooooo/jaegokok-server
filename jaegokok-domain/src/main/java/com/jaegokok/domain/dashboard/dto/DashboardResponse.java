package com.jaegokok.domain.dashboard.dto;

import java.util.List;

public record DashboardResponse(
        long totalProducts,
        long lowStockCount,
        List<LowStockProduct> lowStockProducts,
        long todayInCount,
        long todayOutCount
) {}
