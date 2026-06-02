package com.jaegokok.domain.subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository {
    List<SubscriptionPlan> findAllActive();
    Optional<SubscriptionPlan> findByPlanKey(String planKey);
}
