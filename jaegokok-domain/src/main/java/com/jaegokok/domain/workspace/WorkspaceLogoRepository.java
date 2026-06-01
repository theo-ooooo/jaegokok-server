package com.jaegokok.domain.workspace;

public interface WorkspaceLogoRepository {
    WorkspaceLogo save(Long workspaceId, String originalPath, String webpPath, String bucket);
    void deleteByWorkspaceId(Long workspaceId);
}
