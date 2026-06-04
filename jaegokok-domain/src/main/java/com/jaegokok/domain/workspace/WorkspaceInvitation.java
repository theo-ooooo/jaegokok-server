package com.jaegokok.domain.workspace;

import com.jaegokok.core.workspace.WorkspaceMemberRole;
import java.time.LocalDateTime;

public record WorkspaceInvitation(
        Long id,
        Long workspaceId,
        String email,
        String token,
        LocalDateTime expiresAt,
        boolean used,
        WorkspaceMemberRole role
) {
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
