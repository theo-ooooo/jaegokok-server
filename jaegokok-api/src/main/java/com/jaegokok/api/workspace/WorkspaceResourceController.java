package com.jaegokok.api.workspace;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.core.workspace.WorkspaceMemberRole;
import com.jaegokok.domain.dashboard.DashboardService;
import com.jaegokok.domain.dashboard.dto.DashboardResponse;
import com.jaegokok.domain.inventory.InventoryService;
import com.jaegokok.domain.inventory.dto.InventoryHistoryCondition;
import com.jaegokok.domain.inventory.dto.InventoryHistoryResponse;
import com.jaegokok.domain.product.ProductService;
import com.jaegokok.domain.product.dto.ProductResponse;
import com.jaegokok.domain.product.dto.ProductSearchCondition;
import com.jaegokok.domain.workspace.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}")
@RequiredArgsConstructor
public class WorkspaceResourceController {

    private final ProductService productService;
    private final DashboardService dashboardService;
    private final InventoryService inventoryService;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    private void checkAccess(Long memberId, Long workspaceId) {
        WorkspaceMemberRole role = workspaceMemberRepository.findByWorkspaceIdAndMemberId(workspaceId, memberId)
                .map(wm -> wm.role())
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED));
        if (role == WorkspaceMemberRole.EMPLOYEE) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
    }

    @GetMapping("/products")
    public GlobalResponse<Page<ProductResponse>> getProducts(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workspaceId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean lowStock,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        checkAccess(principal.getId(), workspaceId);
        ProductSearchCondition condition = new ProductSearchCondition(name, category, lowStock);
        return GlobalResponse.success(HttpStatus.OK.value(), productService.findAll(principal.getId(), workspaceId, condition, pageable));
    }

    @GetMapping("/dashboard")
    public GlobalResponse<DashboardResponse> getDashboard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workspaceId
    ) {
        checkAccess(principal.getId(), workspaceId);
        return GlobalResponse.success(HttpStatus.OK.value(), dashboardService.getDashboard(principal.getId(), workspaceId));
    }

    @GetMapping("/inventory/history")
    public GlobalResponse<Page<InventoryHistoryResponse>> getInventoryHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workspaceId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) InventoryType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        checkAccess(principal.getId(), workspaceId);
        InventoryHistoryCondition condition = new InventoryHistoryCondition(productId, type, dateFrom, dateTo);
        return GlobalResponse.success(HttpStatus.OK.value(), inventoryService.getHistory(principal.getId(), workspaceId, condition, pageable));
    }
}
