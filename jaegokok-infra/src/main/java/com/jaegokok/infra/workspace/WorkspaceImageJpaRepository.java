package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkspaceImageJpaRepository extends JpaRepository<WorkspaceImageEntity, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM WorkspaceImageEntity i WHERE i.workspace.id = :workspaceId")
    void deleteByWorkspaceId(@Param("workspaceId") Long workspaceId);
}
