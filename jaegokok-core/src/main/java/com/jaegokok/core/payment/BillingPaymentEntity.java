package com.jaegokok.core.payment;

import com.jaegokok.core.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "billing_payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BillingPaymentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @Column(name = "order_id", nullable = false, unique = true, length = 100)
    private String orderId;

    @Column(name = "payment_key", length = 200)
    private String paymentKey;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "toss_response", columnDefinition = "TEXT")
    private String tossResponse;

    @Column(name = "billing_id")
    private Long billingId;

    public static BillingPaymentEntity create(Long workspaceId, String orderId, Long planId, int amount, Long billingId) {
        BillingPaymentEntity e = new BillingPaymentEntity();
        e.workspaceId = workspaceId;
        e.orderId = orderId;
        e.planId = planId;
        e.amount = amount;
        e.billingId = billingId;
        return e;
    }

    public void confirm(String paymentKey, String tossResponse) {
        this.paymentKey = paymentKey;
        this.tossResponse = tossResponse;
        this.status = "DONE";
    }

    public void fail() {
        this.status = "FAILED";
    }
}
