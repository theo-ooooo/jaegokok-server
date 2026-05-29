package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceJpaRepository extends JpaRepository<WorkspaceEntity, Long> {
}
