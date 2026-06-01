package com.jaegokok.domain.workspace.dto;

import jakarta.validation.constraints.Size;

public record UpdateWorkspaceProfileRequest(
        @Size(max = 200) String companyName,
        @Size(max = 50) String businessNumber,
        @Size(max = 500) String address,
        @Size(max = 50) String phone
) {}
