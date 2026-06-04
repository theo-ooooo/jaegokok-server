package com.jaegokok.api.payment;

import com.jaegokok.domain.payment.Payment;

public record PaymentResponse(
        Long id,
        String orderId,
        String planKey,
        int amount,
        String status
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.id(),
                payment.orderId(),
                payment.planKey(),
                payment.amount(),
                payment.status()
        );
    }
}
