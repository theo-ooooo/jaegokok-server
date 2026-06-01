package com.jaegokok.domain.workspace;

import com.jaegokok.core.workspace.WorkspacePlan;

import java.time.LocalDateTime;

public record Workspace(
        Long id,
        Long ownerId,
        String name,
        String description,
        WorkspacePlan plan,
        String companyName,
        String businessNumber,
        String address,
        String phone,
        WorkspaceLogo logo,
        LocalDateTime createdAt
) {}
