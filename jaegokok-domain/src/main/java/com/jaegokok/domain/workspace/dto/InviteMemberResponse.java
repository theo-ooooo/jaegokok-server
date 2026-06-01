package com.jaegokok.domain.workspace.dto;

import com.jaegokok.core.workspace.WorkspaceMemberRole;
import com.jaegokok.domain.workspace.WorkspaceMember;

public record InviteMemberResponse(
        Long id,
        Long workspaceId,
        Long memberId,
        WorkspaceMemberRole role
) {
    public static InviteMemberResponse from(WorkspaceMember member) {
        return new InviteMemberResponse(member.id(), member.workspaceId(), member.memberId(), member.role());
    }
}
