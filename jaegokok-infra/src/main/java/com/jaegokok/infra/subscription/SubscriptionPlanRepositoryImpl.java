package com.jaegokok.infra.subscription;

import com.jaegokok.core.subscription.SubscriptionPlanEntity;
import com.jaegokok.domain.subscription.SubscriptionPlan;
import com.jaegokok.domain.subscription.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SubscriptionPlanRepositoryImpl implements SubscriptionPlanRepository {

    private final SubscriptionPlanJpaRepository subscriptionPlanJpaRepository;

    @Override
    public List<SubscriptionPlan> findAllActive() {
        return subscriptionPlanJpaRepository.findByIsActiveTrue().stream()
                .map(this::toSubscriptionPlan)
                .toList();
    }

    @Override
    public Optional<SubscriptionPlan> findByPlanKey(String planKey) {
        return subscriptionPlanJpaRepository.findByPlanKey(planKey)
                .map(this::toSubscriptionPlan);
    }

    @Override
    public Optional<SubscriptionPlan> findById(Long id) {
        return subscriptionPlanJpaRepository.findById(id)
                .map(this::toSubscriptionPlan);
    }

    private SubscriptionPlan toSubscriptionPlan(SubscriptionPlanEntity e) {
        return new SubscriptionPlan(
                e.getId(),
                e.getPlanKey(),
                e.getName(),
                e.getPriceKrw(),
                e.getProductLimit(),
                e.getMemberLimit(),
                e.getHistoryDays(),
                e.isActive()
        );
    }
}
