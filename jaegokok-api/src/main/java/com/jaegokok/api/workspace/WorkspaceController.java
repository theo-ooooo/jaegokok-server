package com.jaegokok.api.workspace;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.workspace.WorkspaceService;
import com.jaegokok.domain.workspace.dto.AddEmployeeRequest;
import com.jaegokok.domain.workspace.dto.AddEmployeeResponse;
import com.jaegokok.domain.workspace.dto.CreateWorkspaceRequest;
import com.jaegokok.domain.workspace.dto.UpdateWorkspaceRequest;
import com.jaegokok.domain.workspace.dto.WorkspaceMemberResponse;
import com.jaegokok.domain.workspace.dto.WorkspaceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public GlobalResponse<WorkspaceResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateWorkspaceRequest request
    ) {
        return GlobalResponse.success(HttpStatus.CREATED.value(), workspaceService.create(principal.getId(), request));
    }

    @GetMapping("/me")
    public GlobalResponse<WorkspaceResponse> getMyWorkspace(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return GlobalResponse.success(HttpStatus.OK.value(), workspaceService.getMyWorkspace(principal.getId()));
    }

    @PatchMapping("/me")
    public GlobalResponse<WorkspaceResponse> updateMyWorkspace(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateWorkspaceRequest request
    ) {
        return GlobalResponse.success(HttpStatus.OK.value(), workspaceService.updateMyWorkspace(principal.getId(), request));
    }

    @PostMapping("/{workspaceId}/members")
    public GlobalResponse<AddEmployeeResponse> addEmployee(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workspaceId,
            @Valid @RequestBody AddEmployeeRequest request
    ) {
        return GlobalResponse.success(HttpStatus.CREATED.value(), workspaceService.addEmployee(workspaceId, principal.getId(), request));
    }

    @GetMapping("/{workspaceId}/members")
    public GlobalResponse<List<WorkspaceMemberResponse>> getMembers(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workspaceId
    ) {
        return GlobalResponse.success(HttpStatus.OK.value(), workspaceService.getMembers(workspaceId, principal.getId()));
    }

    @DeleteMapping("/{workspaceId}/members/{memberId}")
    public GlobalResponse<Void> deleteEmployee(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workspaceId,
            @PathVariable Long memberId
    ) {
        workspaceService.deleteEmployee(workspaceId, principal.getId(), memberId);
        return GlobalResponse.success(HttpStatus.OK.value(), null);
    }
}
