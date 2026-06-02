package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceTrialEntity;
import com.jaegokok.domain.workspace.WorkspaceTrial;
import com.jaegokok.domain.workspace.WorkspaceTrialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkspaceTrialRepositoryImpl implements WorkspaceTrialRepository {

    private final WorkspaceTrialJpaRepository workspaceTrialJpaRepository;

    @Override
    public Optional<WorkspaceTrial> findByWorkspaceId(Long workspaceId) {
        return workspaceTrialJpaRepository.findByWorkspaceId(workspaceId)
                .map(this::toWorkspaceTrial);
    }

    @Override
    public WorkspaceTrial save(Long workspaceId) {
        WorkspaceTrialEntity entity = WorkspaceTrialEntity.start(workspaceId);
        return toWorkspaceTrial(workspaceTrialJpaRepository.save(entity));
    }

    private WorkspaceTrial toWorkspaceTrial(WorkspaceTrialEntity e) {
        return new WorkspaceTrial(e.getId(), e.getWorkspaceId(), e.getStartedAt(), e.getEndsAt());
    }
}
