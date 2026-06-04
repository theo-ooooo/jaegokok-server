package com.jaegokok.api.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TossConfirmRequest(
        @NotBlank String paymentKey,
        @NotBlank String orderId,
        @NotNull Integer amount,
        @NotBlank String planKey
) {}
