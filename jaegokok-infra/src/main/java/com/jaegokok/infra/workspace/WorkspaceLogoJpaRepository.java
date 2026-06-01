package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceLogoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkspaceLogoJpaRepository extends JpaRepository<WorkspaceLogoEntity, Long> {

    @Modifying
    @Query("DELETE FROM WorkspaceLogoEntity l WHERE l.workspace.id = :workspaceId")
    void deleteByWorkspaceId(@Param("workspaceId") Long workspaceId);
}
