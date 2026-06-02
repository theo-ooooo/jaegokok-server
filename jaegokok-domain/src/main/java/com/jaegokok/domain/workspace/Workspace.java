package com.jaegokok.domain.workspace;

import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.image.Image;

import java.time.LocalDateTime;

public record Workspace(
        Long id,
        Long ownerId,
        String name,
        String description,
        WorkspacePlan plan,
        String businessNumber,
        String address,
        String phone,
        Image logo,
        LocalDateTime createdAt
) {}
