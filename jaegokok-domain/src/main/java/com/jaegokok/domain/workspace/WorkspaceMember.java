package com.jaegokok.domain.workspace;

import com.jaegokok.core.workspace.WorkspaceMemberRole;

public record WorkspaceMember(
        Long id,
        Long workspaceId,
        Long memberId,
        WorkspaceMemberRole role
) {}
