package com.jaegokok.infra.subscription;

import com.jaegokok.core.subscription.SubscriptionPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanJpaRepository extends JpaRepository<SubscriptionPlanEntity, Long> {
    List<SubscriptionPlanEntity> findByIsActiveTrue();
    Optional<SubscriptionPlanEntity> findByPlanKey(String planKey);
}
