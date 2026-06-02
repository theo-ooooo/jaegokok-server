package com.jaegokok.api.invitation;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.workspace.WorkspaceInvitationRepository;
import com.jaegokok.domain.workspace.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final WorkspaceService workspaceService;
    private final WorkspaceInvitationRepository workspaceInvitationRepository;

    @GetMapping("/info")
    public GlobalResponse<InvitationInfoResponse> getInfo(@RequestParam String token) {
        var invitation = workspaceInvitationRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVITATION_NOT_FOUND));
        if (invitation.used() || invitation.isExpired()) {
            throw new CustomException(ErrorCode.INVITATION_EXPIRED);
        }
        return GlobalResponse.success(HttpStatus.OK.value(), new InvitationInfoResponse(invitation.email()));
    }

    @PostMapping("/accept")
    public GlobalResponse<Void> accept(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid AcceptInvitationRequest request
    ) {
        workspaceService.acceptInvitation(request.token(), principal.getId());
        return GlobalResponse.success(HttpStatus.OK.value(), null);
    }
}
