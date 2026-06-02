package com.jaegokok.domain.workspace;

import java.time.LocalDateTime;

public record WorkspaceInvitation(
        Long id,
        Long workspaceId,
        String email,
        String token,
        LocalDateTime expiresAt,
        boolean used
) {
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
