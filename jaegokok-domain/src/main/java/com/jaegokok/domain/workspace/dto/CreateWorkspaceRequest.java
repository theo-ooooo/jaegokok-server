package com.jaegokok.domain.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description,
        @Pattern(regexp = "^[a-z0-9-]{1,30}$", message = "slug는 소문자, 숫자, 하이픈만 사용 가능하며 최대 30자입니다.") String slug
) {}
