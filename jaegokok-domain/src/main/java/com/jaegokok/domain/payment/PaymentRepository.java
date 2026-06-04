package com.jaegokok.domain.payment;

import java.util.Optional;

public interface PaymentRepository {
    Payment save(Long workspaceId, String orderId, Long planId, int amount, Long billingId);
    Optional<Payment> findByOrderId(String orderId);
    void confirm(Long id, String paymentKey, String tossResponse);
    void fail(Long id);
}
