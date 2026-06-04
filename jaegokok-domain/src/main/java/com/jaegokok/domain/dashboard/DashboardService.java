package com.jaegokok.domain.dashboard;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.domain.dashboard.dto.DashboardResponse;
import com.jaegokok.domain.dashboard.dto.LowStockProduct;
import com.jaegokok.domain.workspace.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final int LOW_STOCK_PREVIEW_LIMIT = 10;

    private final DashboardRepository dashboardRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public DashboardResponse getDashboard(Long memberId, Long workspaceId) {
        if (!workspaceMemberRepository.existsByWorkspaceIdAndMemberId(workspaceId, memberId)) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }

        LocalDate today = LocalDate.now();
        long totalProducts = dashboardRepository.countProducts(workspaceId);
        long lowStockCount = dashboardRepository.countLowStockProducts(workspaceId);
        List<LowStockProduct> lowStockProducts = dashboardRepository.findLowStockProducts(workspaceId, LOW_STOCK_PREVIEW_LIMIT);
        Map<InventoryType, Long> todayCounts = dashboardRepository.countTodayRecordsByType(workspaceId, today);

        return new DashboardResponse(
                totalProducts,
                lowStockCount,
                lowStockProducts,
                todayCounts.getOrDefault(InventoryType.IN, 0L),
                todayCounts.getOrDefault(InventoryType.OUT, 0L)
        );
    }
}
