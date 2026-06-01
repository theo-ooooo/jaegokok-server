package com.jaegokok.domain.workspace.dto;

import com.jaegokok.domain.workspace.WorkspaceImage;

public record WorkspaceImageResponse(
        Long id,
        String originalPath,
        String webpPath
) {
    public static WorkspaceImageResponse from(WorkspaceImage image) {
        return new WorkspaceImageResponse(image.id(), image.originalPath(), image.webpPath());
    }
}
