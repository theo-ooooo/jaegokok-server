package com.jaegokok.domain.workspace;

public interface WorkspaceImageRepository {
    WorkspaceImage save(Long workspaceId, String originalPath, String webpPath, String bucket);
    void deleteByWorkspaceId(Long workspaceId);
}
