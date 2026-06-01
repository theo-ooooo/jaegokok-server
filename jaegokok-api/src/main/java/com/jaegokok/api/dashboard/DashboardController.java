package com.jaegokok.api.dashboard;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.dashboard.DashboardService;
import com.jaegokok.domain.dashboard.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public GlobalResponse<DashboardResponse> getDashboard(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return GlobalResponse.success(HttpStatus.OK.value(), dashboardService.getDashboard(principal.getId()));
    }
}
