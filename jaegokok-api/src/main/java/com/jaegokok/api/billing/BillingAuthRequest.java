package com.jaegokok.api.billing;

import jakarta.validation.constraints.NotBlank;

public record BillingAuthRequest(
        @NotBlank String authKey,
        @NotBlank String customerKey,
        @NotBlank String planKey
) {}
