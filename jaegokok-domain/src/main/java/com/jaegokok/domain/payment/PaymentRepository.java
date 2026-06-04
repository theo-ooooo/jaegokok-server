package com.jaegokok.domain.payment;

import java.util.Optional;

public interface PaymentRepository {
    Payment save(Long workspaceId, String orderId, String planKey, int amount);
    Optional<Payment> findByOrderId(String orderId);
    void confirm(Long id, String paymentKey, String tossResponse);
    void fail(Long id);
}
