package com.jaegokok.domain.dashboard;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.domain.dashboard.dto.DashboardResponse;
import com.jaegokok.domain.dashboard.dto.LowStockProduct;
import com.jaegokok.domain.workspace.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final WorkspaceRepository workspaceRepository;

    public DashboardResponse getDashboard(Long memberId) {
        Long workspaceId = workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND))
                .id();

        LocalDate today = LocalDate.now();
        long totalProducts = dashboardRepository.countProducts(workspaceId);
        List<LowStockProduct> lowStockProducts = dashboardRepository.findLowStockProducts(workspaceId);
        long todayInCount = dashboardRepository.countTodayRecords(workspaceId, InventoryType.IN, today);
        long todayOutCount = dashboardRepository.countTodayRecords(workspaceId, InventoryType.OUT, today);

        return new DashboardResponse(
                totalProducts,
                lowStockProducts.size(),
                lowStockProducts,
                todayInCount,
                todayOutCount
        );
    }
}
