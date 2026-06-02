package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceTrialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceTrialJpaRepository extends JpaRepository<WorkspaceTrialEntity, Long> {
    Optional<WorkspaceTrialEntity> findByWorkspaceId(Long workspaceId);
}
