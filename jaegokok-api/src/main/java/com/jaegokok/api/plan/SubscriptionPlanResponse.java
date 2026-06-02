package com.jaegokok.api.plan;

import com.jaegokok.domain.subscription.SubscriptionPlan;

public record SubscriptionPlanResponse(
    String planKey, String name, int priceKrw,
    int productLimit, int memberLimit, int historyDays
) {
    public static SubscriptionPlanResponse from(SubscriptionPlan p) {
        return new SubscriptionPlanResponse(p.planKey(), p.name(), p.priceKrw(), p.productLimit(), p.memberLimit(), p.historyDays());
    }
}
