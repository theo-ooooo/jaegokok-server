package com.jaegokok.api.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteByEmailRequest(@NotBlank @Email String email) {}
