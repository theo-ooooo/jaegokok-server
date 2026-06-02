package com.jaegokok.domain.workspace.dto;

import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.image.dto.ImageResponse;
import com.jaegokok.domain.workspace.Workspace;
import com.jaegokok.domain.workspace.WorkspaceTrial;

import java.time.LocalDateTime;
import java.util.Optional;

public record WorkspaceResponse(
        Long id,
        String name,
        String description,
        WorkspacePlan plan,
        String companyName,
        String businessNumber,
        String address,
        String phone,
        ImageResponse logo,
        LocalDateTime createdAt,
        boolean isOnTrial,
        int trialDaysLeft
) {
    public static WorkspaceResponse from(Workspace workspace, Optional<WorkspaceTrial> trial) {
        boolean isOnTrial = trial.map(WorkspaceTrial::isActive).orElse(false);
        int trialDaysLeft = trial.map(WorkspaceTrial::daysLeft).orElse(0);
        return new WorkspaceResponse(
                workspace.id(),
                workspace.name(),
                workspace.description(),
                workspace.plan(),
                workspace.companyName(),
                workspace.businessNumber(),
                workspace.address(),
                workspace.phone(),
                workspace.logo() != null ? ImageResponse.from(workspace.logo()) : null,
                workspace.createdAt(),
                isOnTrial,
                trialDaysLeft
        );
    }
}
