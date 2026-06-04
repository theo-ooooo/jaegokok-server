package com.jaegokok.domain.workspace.dto;

import com.jaegokok.domain.file.FileUploadPort;
import com.jaegokok.domain.workspace.Workspace;

public record PublicWorkspaceResponse(
        String name,
        String slug,
        String logoUrl
) {
    public static PublicWorkspaceResponse from(Workspace workspace, FileUploadPort fileUploadPort) {
        String logoUrl = workspace.logo() != null
                ? (workspace.logo().webpPath() != null
                    ? fileUploadPort.toUrl(workspace.logo().webpPath())
                    : fileUploadPort.toUrl(workspace.logo().originalPath()))
                : null;
        return new PublicWorkspaceResponse(workspace.name(), workspace.slug(), logoUrl);
    }
}
