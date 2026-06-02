package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceMemberJpaRepository extends JpaRepository<WorkspaceMemberEntity, Long> {
    boolean existsByWorkspace_IdAndMember_Id(Long workspaceId, Long memberId);
    java.util.Optional<WorkspaceMemberEntity> findFirstByMember_Id(Long memberId);
}
