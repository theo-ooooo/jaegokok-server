package com.jaegokok.domain.workspace;

import java.util.Optional;

public interface WorkspaceTrialRepository {
    Optional<WorkspaceTrial> findByWorkspaceId(Long workspaceId);
    WorkspaceTrial save(Long workspaceId);
}
