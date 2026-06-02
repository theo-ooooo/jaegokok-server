package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceInvitationJpaRepository extends JpaRepository<WorkspaceInvitationEntity, Long> {
    Optional<WorkspaceInvitationEntity> findByToken(String token);
}
