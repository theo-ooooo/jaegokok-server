package com.jaegokok.domain.workspace.dto;

public record WorkspaceSummaryResponse(
        Long id,
        String name,
        String slug,
        String myRole
) {}
