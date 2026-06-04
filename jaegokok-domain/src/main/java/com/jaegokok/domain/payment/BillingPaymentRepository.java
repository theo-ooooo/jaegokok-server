package com.jaegokok.domain.payment;

import java.util.Optional;

public interface BillingPaymentRepository {
    BillingPayment save(Long workspaceId, String orderId, Long planId, int amount, Long billingId);
    Optional<BillingPayment> findByOrderId(String orderId);
    void confirm(Long id, String paymentKey, String tossResponse);
    void fail(Long id);
}
