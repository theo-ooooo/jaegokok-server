package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceEntity;

import java.util.List;

public interface WorkspaceQueryRepository {
    List<WorkspaceEntity> findAllByMemberId(Long memberId);
}
