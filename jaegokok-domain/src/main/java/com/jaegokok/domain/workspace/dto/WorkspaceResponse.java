package com.jaegokok.domain.workspace.dto;

import com.jaegokok.core.workspace.WorkspaceMemberRole;
import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.image.dto.ImageResponse;
import com.jaegokok.domain.workspace.Workspace;
import com.jaegokok.domain.workspace.WorkspaceMember;
import com.jaegokok.domain.workspace.WorkspaceTrial;

import java.time.LocalDateTime;
import java.util.Optional;

public record WorkspaceResponse(
        Long id,
        Long ownerId,
        String myRole,
        String name,
        String description,
        WorkspacePlan plan,
        String businessNumber,
        String address,
        String phone,
        ImageResponse logo,
        LocalDateTime createdAt,
        boolean isOnTrial,
        int trialDaysLeft
) {
    public static WorkspaceResponse from(Workspace workspace, Optional<WorkspaceTrial> trial) {
        return from(workspace, trial, null);
    }

    public static WorkspaceResponse from(Workspace workspace, Optional<WorkspaceTrial> trial, WorkspaceMember membership) {
        boolean isOnTrial = trial.map(WorkspaceTrial::isActive).orElse(false);
        int trialDaysLeft = trial.map(WorkspaceTrial::daysLeft).orElse(0);
        String myRole = membership != null ? membership.role().name()
                : workspace.ownerId() != null ? WorkspaceMemberRole.OWNER.name() : null;
        return new WorkspaceResponse(
                workspace.id(),
                workspace.ownerId(),
                myRole,
                workspace.name(),
                workspace.description(),
                workspace.plan(),
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
