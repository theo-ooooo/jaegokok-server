package com.jaegokok.domain.workspace.dto;

import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.workspace.Workspace;

import java.time.LocalDateTime;

public record WorkspaceResponse(
        Long id,
        String name,
        String description,
        WorkspacePlan plan,
        String companyName,
        String businessNumber,
        String address,
        String phone,
        WorkspaceImageResponse logo,
        LocalDateTime createdAt
) {
    public static WorkspaceResponse from(Workspace workspace) {
        return new WorkspaceResponse(
                workspace.id(),
                workspace.name(),
                workspace.description(),
                workspace.plan(),
                workspace.companyName(),
                workspace.businessNumber(),
                workspace.address(),
                workspace.phone(),
                workspace.logo() != null ? WorkspaceImageResponse.from(workspace.logo()) : null,
                workspace.createdAt()
        );
    }
}
