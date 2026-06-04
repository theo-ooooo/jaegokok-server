package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMemberJpaRepository extends JpaRepository<WorkspaceMemberEntity, Long> {
    boolean existsByWorkspace_IdAndMember_Id(Long workspaceId, Long memberId);
    Optional<WorkspaceMemberEntity> findFirstByMember_Id(Long memberId);
    List<WorkspaceMemberEntity> findAllByMember_Id(Long memberId);
    long countByWorkspace_Id(Long workspaceId);
}
