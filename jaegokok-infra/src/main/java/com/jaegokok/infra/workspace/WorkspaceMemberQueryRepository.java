package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceMemberEntity;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMemberQueryRepository {
    List<WorkspaceMemberEntity> findByWorkspaceId(Long workspaceId);
    Optional<WorkspaceMemberEntity> findByWorkspaceIdAndMemberId(Long workspaceId, Long memberId);
}
