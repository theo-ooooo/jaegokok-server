package com.jaegokok.domain.payment;

public record Payment(
        Long id,
        Long workspaceId,
        String orderId,
        String paymentKey,
        Long planId,
        int amount,
        String status,
        Long billingId
) {}
