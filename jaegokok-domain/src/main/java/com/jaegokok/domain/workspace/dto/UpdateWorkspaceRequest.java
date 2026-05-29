package com.jaegokok.domain.workspace.dto;

import jakarta.validation.constraints.Size;

public record UpdateWorkspaceRequest(
        @Size(max = 100) String name,
        @Size(max = 500) String description
) {}
