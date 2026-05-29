package com.jaegokok.domain.workspace;

import com.jaegokok.core.workspace.WorkspaceMemberRole;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMemberRepository {
    WorkspaceMember save(Long workspaceId, Long memberId, WorkspaceMemberRole role);
    List<WorkspaceMember> findByWorkspaceId(Long workspaceId);
    Optional<WorkspaceMember> findByWorkspaceIdAndMemberId(Long workspaceId, Long memberId);
    void deleteById(Long id);
}
