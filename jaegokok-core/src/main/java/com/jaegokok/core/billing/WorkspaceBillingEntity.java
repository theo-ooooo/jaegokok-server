package com.jaegokok.core.billing;

import com.jaegokok.core.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "workspace_billings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkspaceBillingEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workspace_id", nullable = false, unique = true)
    private Long workspaceId;

    @Column(name = "billing_key", nullable = false, length = 300)
    private String billingKey;

    @Column(name = "customer_key", nullable = false, length = 100)
    private String customerKey;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "next_billing_date", nullable = false)
    private LocalDate nextBillingDate;

    public static WorkspaceBillingEntity create(Long workspaceId, String billingKey, String customerKey, Long planId) {
        WorkspaceBillingEntity e = new WorkspaceBillingEntity();
        e.workspaceId = workspaceId;
        e.billingKey = billingKey;
        e.customerKey = customerKey;
        e.planId = planId;
        e.status = "ACTIVE";
        e.nextBillingDate = LocalDate.now().plusMonths(1);
        return e;
    }

    public static WorkspaceBillingEntity createPending(Long workspaceId, String billingKey, String customerKey, Long planId) {
        WorkspaceBillingEntity e = new WorkspaceBillingEntity();
        e.workspaceId = workspaceId;
        e.billingKey = billingKey;
        e.customerKey = customerKey;
        e.planId = planId;
        e.status = "PENDING";
        e.nextBillingDate = LocalDate.now().plusMonths(1);
        return e;
    }

    public void activate() {
        this.status = "ACTIVE";
        this.nextBillingDate = LocalDate.now().plusMonths(1);
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    public void renewNextDate() {
        this.nextBillingDate = this.nextBillingDate.plusMonths(1);
    }
}
