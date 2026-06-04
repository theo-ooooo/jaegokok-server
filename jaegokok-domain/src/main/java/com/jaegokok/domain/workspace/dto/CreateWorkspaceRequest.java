package com.jaegokok.domain.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description,
        @Pattern(regexp = "^[a-z0-9][a-z0-9-]{1,28}[a-z0-9]$", message = "slug는 소문자/숫자로 시작·끝, 하이픈 포함 3~30자입니다.") String slug
) {}
