package com.jaegokok.domain.payment;

public record Payment(
        Long id,
        Long workspaceId,
        String orderId,
        String paymentKey,
        String planKey,
        int amount,
        String status
) {}
