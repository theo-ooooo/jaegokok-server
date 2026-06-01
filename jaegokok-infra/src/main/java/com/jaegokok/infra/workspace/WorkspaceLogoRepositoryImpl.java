package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceLogoEntity;
import com.jaegokok.domain.workspace.WorkspaceLogo;
import com.jaegokok.domain.workspace.WorkspaceLogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class WorkspaceLogoRepositoryImpl implements WorkspaceLogoRepository {

    private final WorkspaceLogoJpaRepository workspaceLogoJpaRepository;
    private final WorkspaceJpaRepository workspaceJpaRepository;

    @Override
    public WorkspaceLogo save(Long workspaceId, String originalPath, String webpPath, String bucket) {
        WorkspaceLogoEntity entity = workspaceLogoJpaRepository.save(
                WorkspaceLogoEntity.of(workspaceJpaRepository.getReferenceById(workspaceId), originalPath, webpPath, bucket));
        return toWorkspaceLogo(entity);
    }

    @Override
    @Transactional
    public void deleteByWorkspaceId(Long workspaceId) {
        workspaceLogoJpaRepository.deleteByWorkspaceId(workspaceId);
    }

    private WorkspaceLogo toWorkspaceLogo(WorkspaceLogoEntity e) {
        return new WorkspaceLogo(e.getId(), e.getWorkspace().getId(), e.getOriginalPath(), e.getWebpPath(), e.getBucket(), e.getCreatedAt());
    }
}
