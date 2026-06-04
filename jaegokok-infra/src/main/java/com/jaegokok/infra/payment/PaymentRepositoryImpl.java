package com.jaegokok.infra.payment;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.payment.PaymentEntity;
import com.jaegokok.domain.payment.Payment;
import com.jaegokok.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Long workspaceId, String orderId, String planKey, int amount) {
        PaymentEntity entity = PaymentEntity.create(workspaceId, orderId, planKey, amount);
        return toPayment(paymentJpaRepository.save(entity));
    }

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return paymentJpaRepository.findByOrderId(orderId).map(this::toPayment);
    }

    @Override
    public void confirm(Long id, String paymentKey, String tossResponse) {
        PaymentEntity entity = paymentJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        entity.confirm(paymentKey, tossResponse);
    }

    @Override
    public void fail(Long id) {
        PaymentEntity entity = paymentJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        entity.fail();
    }

    private Payment toPayment(PaymentEntity e) {
        return new Payment(e.getId(), e.getWorkspaceId(), e.getOrderId(), e.getPaymentKey(),
                e.getPlanKey(), e.getAmount(), e.getStatus());
    }
}
