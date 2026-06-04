package com.jaegokok.domain.payment;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.subscription.SubscriptionPlanRepository;
import com.jaegokok.domain.workspace.Workspace;
import com.jaegokok.domain.workspace.WorkspaceRepository;
import com.jaegokok.domain.workspace.WorkspaceTrialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentPort tossPaymentPort;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceTrialRepository workspaceTrialRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public Payment confirmPayment(Long memberId, String paymentKey, String orderId, int amount, String planKey) {
        // 1. Validate amount against subscription plan price
        int expectedAmount = subscriptionPlanRepository.findByPlanKey(planKey)
                .map(sp -> sp.priceKrw())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));
        if (amount != expectedAmount) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        // 2. Find or create payment record
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseGet(() -> {
                    Workspace ws = workspaceRepository.findByOwnerId(memberId)
                            .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
                    return paymentRepository.save(ws.id(), orderId, planKey, amount);
                });

        // 3. Call Toss Payments API
        TossPaymentPort.TossConfirmResult result = tossPaymentPort.confirm(paymentKey, orderId, amount);

        if (result.success()) {
            paymentRepository.confirm(payment.id(), paymentKey, result.message());
            // 4. Upgrade workspace plan
            WorkspacePlan newPlan = WorkspacePlan.valueOf(planKey);
            workspaceRepository.updatePlan(payment.workspaceId(), newPlan);
        } else {
            paymentRepository.fail(payment.id());
            throw new CustomException(ErrorCode.PAYMENT_FAILED);
        }

        return paymentRepository.findByOrderId(orderId).orElseThrow();
    }
}
