package com.jaegokok.domain.workspace.dto;

import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.workspace.Workspace;

import java.time.LocalDateTime;

public record WorkspaceResponse(
        Long id,
        String name,
        String description,
        WorkspacePlan plan,
        LocalDateTime createdAt
) {
    public static WorkspaceResponse from(Workspace workspace) {
        return new WorkspaceResponse(
                workspace.id(),
                workspace.name(),
                workspace.description(),
                workspace.plan(),
                workspace.createdAt()
        );
    }
}
