package com.jaegokok.infra.payment;

import com.jaegokok.core.payment.BillingPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillingPaymentJpaRepository extends JpaRepository<BillingPaymentEntity, Long> {
    Optional<BillingPaymentEntity> findByOrderId(String orderId);
}
