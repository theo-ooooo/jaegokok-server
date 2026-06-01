package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceImageEntity;
import com.jaegokok.domain.workspace.WorkspaceImage;
import com.jaegokok.domain.workspace.WorkspaceImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class WorkspaceImageRepositoryImpl implements WorkspaceImageRepository {

    private final WorkspaceImageJpaRepository workspaceImageJpaRepository;
    private final WorkspaceJpaRepository workspaceJpaRepository;

    @Override
    public WorkspaceImage save(Long workspaceId, String originalPath, String webpPath, String bucket) {
        WorkspaceImageEntity entity = workspaceImageJpaRepository.save(
                WorkspaceImageEntity.of(workspaceJpaRepository.getReferenceById(workspaceId), originalPath, webpPath, bucket));
        return toWorkspaceImage(entity);
    }

    @Override
    @Transactional
    public void deleteByWorkspaceId(Long workspaceId) {
        workspaceImageJpaRepository.deleteByWorkspaceId(workspaceId);
    }

    private WorkspaceImage toWorkspaceImage(WorkspaceImageEntity e) {
        return new WorkspaceImage(e.getId(), e.getWorkspace().getId(), e.getOriginalPath(), e.getWebpPath(), e.getBucket(), e.getCreatedAt());
    }
}
