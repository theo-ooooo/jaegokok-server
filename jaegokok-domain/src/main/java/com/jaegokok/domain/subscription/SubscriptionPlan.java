package com.jaegokok.domain.subscription;

public record SubscriptionPlan(
        Long id,
        String planKey,
        String name,
        int priceKrw,
        int productLimit,
        int memberLimit,
        int historyDays,
        boolean isActive
) {
    public boolean isUnlimitedProducts() {
        return productLimit < 0;
    }
}
