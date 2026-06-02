package com.jaegokok.api.invitation;

import jakarta.validation.constraints.NotBlank;

public record AcceptInvitationRequest(@NotBlank String token) {}
