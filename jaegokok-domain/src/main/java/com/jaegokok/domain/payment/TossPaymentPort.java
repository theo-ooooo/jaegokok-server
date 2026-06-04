package com.jaegokok.domain.payment;

public interface TossPaymentPort {
    TossConfirmResult confirm(String paymentKey, String orderId, int amount);

    BillingKeyResult issueBillingKey(String authKey, String customerKey);

    TossConfirmResult chargeWithBillingKey(String billingKey, String orderId, String orderName, int amount, String customerKey);

    record TossConfirmResult(boolean success, String status, String message) {}

    record BillingKeyResult(boolean success, String billingKey, String message) {}
}
