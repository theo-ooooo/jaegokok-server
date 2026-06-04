package com.jaegokok.infra.payment;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.payment.BillingPaymentEntity;
import com.jaegokok.domain.payment.BillingPayment;
import com.jaegokok.domain.payment.BillingPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BillingPaymentRepositoryImpl implements BillingPaymentRepository {

    private final BillingPaymentJpaRepository billingPaymentJpaRepository;

    @Override
    public BillingPayment save(Long workspaceId, String orderId, Long planId, int amount, Long billingId) {
        BillingPaymentEntity entity = BillingPaymentEntity.create(workspaceId, orderId, planId, amount, billingId);
        return toBillingPayment(billingPaymentJpaRepository.save(entity));
    }

    @Override
    public Optional<BillingPayment> findByOrderId(String orderId) {
        return billingPaymentJpaRepository.findByOrderId(orderId).map(this::toBillingPayment);
    }

    @Override
    public void confirm(Long id, String paymentKey, String tossResponse) {
        BillingPaymentEntity entity = billingPaymentJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        entity.confirm(paymentKey, tossResponse);
    }

    @Override
    public void fail(Long id) {
        BillingPaymentEntity entity = billingPaymentJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        entity.fail();
    }

    private BillingPayment toBillingPayment(BillingPaymentEntity e) {
        return new BillingPayment(e.getId(), e.getWorkspaceId(), e.getOrderId(), e.getPaymentKey(),
                e.getPlanId(), e.getAmount(), e.getStatus(), e.getBillingId());
    }
}
