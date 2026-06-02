package com.jaegokok.api.member;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.member.MemberService;
import com.jaegokok.domain.member.dto.MemberResponse;
import com.jaegokok.domain.workspace.WorkspaceService;
import com.jaegokok.domain.workspace.dto.InviteMemberRequest;
import com.jaegokok.domain.workspace.dto.InviteMemberResponse;
import com.jaegokok.domain.workspace.dto.UpdateMemberRoleRequest;
import com.jaegokok.domain.workspace.dto.WorkspaceMemberResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final WorkspaceService workspaceService;

    @GetMapping("/me")
    public GlobalResponse<MemberResponse> getMe(@AuthenticationPrincipal UserPrincipal principal) {
        return GlobalResponse.success(HttpStatus.OK.value(), memberService.getMe(principal.getId()));
    }

    @GetMapping
    public GlobalResponse<List<WorkspaceMemberResponse>> listMembers(@AuthenticationPrincipal UserPrincipal principal) {
        return GlobalResponse.success(HttpStatus.OK.value(), workspaceService.listMembers(principal.getId()));
    }

    @PostMapping("/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalResponse<InviteMemberResponse> invite(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody InviteMemberRequest request
    ) {
        return GlobalResponse.success(HttpStatus.CREATED.value(), workspaceService.inviteMember(principal.getId(), request));
    }

    @PatchMapping("/{id}")
    public GlobalResponse<WorkspaceMemberResponse> updateMemberRole(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberRoleRequest request
    ) {
        return GlobalResponse.success(HttpStatus.OK.value(), workspaceService.updateMemberRole(principal.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        workspaceService.removeMember(principal.getId(), id);
    }

    @DeleteMapping("/me")
    public GlobalResponse<Void> withdraw(@AuthenticationPrincipal UserPrincipal principal) {
        memberService.withdraw(principal.getId());
        return GlobalResponse.success(HttpStatus.OK.value(), null);
    }
}
