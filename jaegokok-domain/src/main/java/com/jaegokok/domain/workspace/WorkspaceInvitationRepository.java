package com.jaegokok.domain.workspace;

import java.util.Optional;

public interface WorkspaceInvitationRepository {
    WorkspaceInvitation save(Long workspaceId, String email);
    Optional<WorkspaceInvitation> findByToken(String token);
    void markUsed(Long id);
}
