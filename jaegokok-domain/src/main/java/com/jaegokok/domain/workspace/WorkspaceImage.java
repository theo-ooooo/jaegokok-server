package com.jaegokok.domain.workspace;

import java.time.LocalDateTime;

public record WorkspaceImage(
        Long id,
        Long workspaceId,
        String originalPath,
        String webpPath,
        String bucket,
        LocalDateTime createdAt
) {}
