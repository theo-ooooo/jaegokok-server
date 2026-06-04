package com.jaegokok.domain.workspace;

import com.jaegokok.core.workspace.WorkspaceMemberRole;
import java.util.Optional;

public interface WorkspaceInvitationRepository {
    WorkspaceInvitation save(Long workspaceId, String email, WorkspaceMemberRole role);
    Optional<WorkspaceInvitation> findByToken(String token);
    void markUsed(Long id);
    boolean markUsedByToken(String token);
}
