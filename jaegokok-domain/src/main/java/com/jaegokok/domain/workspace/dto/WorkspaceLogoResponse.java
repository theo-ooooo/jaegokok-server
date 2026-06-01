package com.jaegokok.domain.workspace.dto;

import com.jaegokok.domain.workspace.WorkspaceLogo;

public record WorkspaceLogoResponse(
        Long id,
        String originalPath,
        String webpPath
) {
    public static WorkspaceLogoResponse from(WorkspaceLogo logo) {
        return new WorkspaceLogoResponse(logo.id(), logo.originalPath(), logo.webpPath());
    }
}
