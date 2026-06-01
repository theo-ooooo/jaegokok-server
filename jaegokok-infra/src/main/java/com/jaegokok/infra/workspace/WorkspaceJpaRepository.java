package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceJpaRepository extends JpaRepository<WorkspaceEntity, Long> {
    boolean existsByOwner_Id(Long ownerId);
    Optional<WorkspaceEntity> findByOwner_Id(Long ownerId);
}
