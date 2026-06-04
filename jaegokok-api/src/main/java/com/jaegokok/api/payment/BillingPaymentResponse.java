package com.jaegokok.api.payment;

import com.jaegokok.domain.payment.BillingPayment;

public record BillingPaymentResponse(
        Long id,
        String orderId,
        Long planId,
        int amount,
        String status,
        Long billingId
) {
    public static BillingPaymentResponse from(BillingPayment payment) {
        return new BillingPaymentResponse(
                payment.id(),
                payment.orderId(),
                payment.planId(),
                payment.amount(),
                payment.status(),
                payment.billingId()
        );
    }
}
