package com.jaegokok.domain.workspace.dto;

import com.jaegokok.core.member.MemberStatus;
import com.jaegokok.core.workspace.WorkspaceMemberRole;

public record WorkspaceMemberResponse(
        Long id,
        Long memberId,
        String nickname,
        String email,
        WorkspaceMemberRole role,
        MemberStatus status
) {}
