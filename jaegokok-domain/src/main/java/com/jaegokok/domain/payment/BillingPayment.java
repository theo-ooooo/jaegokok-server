package com.jaegokok.domain.payment;

public record BillingPayment(
        Long id,
        Long workspaceId,
        String orderId,
        String paymentKey,
        Long planId,
        int amount,
        String status,
        Long billingId
) {}
