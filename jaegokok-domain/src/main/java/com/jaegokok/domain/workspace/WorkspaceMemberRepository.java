package com.jaegokok.domain.workspace;

import com.jaegokok.core.workspace.WorkspaceMemberRole;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMemberRepository {
    WorkspaceMember save(Long workspaceId, Long memberId, WorkspaceMemberRole role);
    List<WorkspaceMember> findByWorkspaceId(Long workspaceId);
    Optional<WorkspaceMember> findById(Long id);
    Optional<WorkspaceMember> findByMemberId(Long memberId);
    Optional<WorkspaceMember> findByWorkspaceIdAndMemberId(Long workspaceId, Long memberId);
    WorkspaceMember updateRole(Long id, WorkspaceMemberRole role);
    void deleteById(Long id);
    boolean existsByWorkspaceIdAndMemberId(Long workspaceId, Long memberId);
}
