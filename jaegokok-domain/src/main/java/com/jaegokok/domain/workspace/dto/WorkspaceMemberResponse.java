package com.jaegokok.domain.workspace.dto;

import com.jaegokok.core.workspace.WorkspaceMemberRole;
import com.jaegokok.domain.member.Member;
import com.jaegokok.domain.workspace.WorkspaceMember;

public record WorkspaceMemberResponse(
        Long workspaceMemberId,
        Long memberId,
        String email,
        String nickname,
        WorkspaceMemberRole role
) {
    public static WorkspaceMemberResponse of(WorkspaceMember wm, Member member) {
        return new WorkspaceMemberResponse(wm.id(), member.id(), member.email(), member.nickname(), wm.role());
    }
}
