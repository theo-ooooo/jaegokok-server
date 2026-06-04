package com.jaegokok.api.billing;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.billing.BillingService;
import com.jaegokok.domain.workspace.WorkspaceService;
import com.jaegokok.domain.workspace.dto.WorkspaceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final WorkspaceService workspaceService;
    private final BillingService billingService;

    @PostMapping("/trial")
    public GlobalResponse<WorkspaceResponse> startTrial(@AuthenticationPrincipal UserPrincipal principal) {
        return GlobalResponse.success(HttpStatus.OK.value(), workspaceService.startTrial(principal.getId()));
    }

    @PostMapping("/auth")
    public GlobalResponse<Void> activateBilling(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody BillingAuthRequest request) {
        billingService.activateBilling(principal.getId(), request.authKey(), request.customerKey(), request.planKey());
        return GlobalResponse.success(HttpStatus.OK.value(), null);
    }

    @DeleteMapping
    public GlobalResponse<Void> cancelBilling(@AuthenticationPrincipal UserPrincipal principal) {
        billingService.cancelBilling(principal.getId());
        return GlobalResponse.success(HttpStatus.OK.value(), null);
    }
}
