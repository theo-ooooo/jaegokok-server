package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkspaceInvitationJpaRepository extends JpaRepository<WorkspaceInvitationEntity, Long> {
    Optional<WorkspaceInvitationEntity> findByToken(String token);

    @Modifying
    @Query("UPDATE WorkspaceInvitationEntity w SET w.used = true WHERE w.token = :token AND w.used = false")
    int markUsedByToken(@Param("token") String token);
}
