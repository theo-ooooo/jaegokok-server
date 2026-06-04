package com.jaegokok.api.member;

import com.jaegokok.core.workspace.WorkspaceMemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteByEmailRequest(@NotBlank @Email String email, WorkspaceMemberRole role) {
    public WorkspaceMemberRole role() {
        return role != null ? role : WorkspaceMemberRole.EMPLOYEE;
    }
}
