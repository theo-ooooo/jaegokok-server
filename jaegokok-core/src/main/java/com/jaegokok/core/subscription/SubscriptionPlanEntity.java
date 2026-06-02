package com.jaegokok.core.subscription;

import com.jaegokok.core.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionPlanEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_key", nullable = false, unique = true, length = 20)
    private String planKey;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "price_krw", nullable = false)
    private int priceKrw;

    @Column(name = "product_limit", nullable = false)
    private int productLimit;

    @Column(name = "member_limit", nullable = false)
    private int memberLimit;

    @Column(name = "history_days", nullable = false)
    private int historyDays;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public static SubscriptionPlanEntity create(String planKey, String name, int priceKrw, int productLimit, int memberLimit, int historyDays) {
        SubscriptionPlanEntity e = new SubscriptionPlanEntity();
        e.planKey = planKey;
        e.name = name;
        e.priceKrw = priceKrw;
        e.productLimit = productLimit;
        e.memberLimit = memberLimit;
        e.historyDays = historyDays;
        e.isActive = true;
        return e;
    }
}
