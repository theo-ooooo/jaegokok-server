package com.jaegokok.domain.workspace.dto;

import com.jaegokok.core.workspace.WorkspaceMemberRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
        @NotNull WorkspaceMemberRole role
) {}
