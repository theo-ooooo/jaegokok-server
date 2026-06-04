package com.jaegokok.domain.billing;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.payment.Payment;
import com.jaegokok.domain.payment.PaymentRepository;
import com.jaegokok.domain.payment.TossPaymentPort;
import com.jaegokok.domain.subscription.SubscriptionPlan;
import com.jaegokok.domain.subscription.SubscriptionPlanRepository;
import com.jaegokok.domain.workspace.Workspace;
import com.jaegokok.domain.workspace.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BillingService {

    private final WorkspaceBillingRepository workspaceBillingRepository;
    private final PaymentRepository paymentRepository;
    private final TossPaymentPort tossPaymentPort;
    private final WorkspaceRepository workspaceRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public void activateBilling(Long memberId, String authKey, String customerKey, String planKey) {
        Workspace workspace = workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));

        workspaceBillingRepository.findByWorkspaceId(workspace.id())
                .filter(b -> "ACTIVE".equals(b.status()))
                .ifPresent(b -> { throw new CustomException(ErrorCode.ALREADY_ON_PAID_PLAN); });

        SubscriptionPlan plan = subscriptionPlanRepository.findByPlanKey(planKey)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        TossPaymentPort.BillingKeyResult keyResult = tossPaymentPort.issueBillingKey(authKey, customerKey);
        if (!keyResult.success()) throw new CustomException(ErrorCode.PAYMENT_FAILED);

        String orderId = "JAEGOK-" + planKey + "-" + System.currentTimeMillis();
        String orderName = "재고콕 " + plan.name() + " 플랜";

        TossPaymentPort.TossConfirmResult chargeResult = tossPaymentPort.chargeWithBillingKey(
                keyResult.billingKey(), orderId, orderName, plan.priceKrw(), customerKey);

        if (!chargeResult.success()) throw new CustomException(ErrorCode.PAYMENT_FAILED);

        workspaceBillingRepository.save(workspace.id(), keyResult.billingKey(), customerKey, plan.id());

        Payment payment = paymentRepository.save(workspace.id(), orderId, plan.id(), plan.priceKrw());
        paymentRepository.confirm(payment.id(), orderId, chargeResult.message());

        workspaceRepository.updatePlan(workspace.id(), WorkspacePlan.valueOf(planKey));
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void processScheduledBillings() {
        List<WorkspaceBilling> due = workspaceBillingRepository.findAllDueForBilling(LocalDate.now());
        for (WorkspaceBilling billing : due) {
            try {
                SubscriptionPlan plan = subscriptionPlanRepository.findById(billing.planId()).orElse(null);
                if (plan == null) continue;
                String orderId = "JAEGOK-RENEW-" + plan.planKey() + "-" + System.currentTimeMillis();
                String orderName = "재고콕 " + plan.name() + " 플랜 (갱신)";
                TossPaymentPort.TossConfirmResult result = tossPaymentPort.chargeWithBillingKey(
                        billing.billingKey(), orderId, orderName, plan.priceKrw(), billing.customerKey());
                if (result.success()) {
                    workspaceBillingRepository.renewNextDate(billing.id());
                    Payment payment = paymentRepository.save(billing.workspaceId(), orderId, billing.planId(), plan.priceKrw());
                    paymentRepository.confirm(payment.id(), orderId, result.message());
                }
            } catch (Exception e) {
                log.warn("Billing failed for workspace {}: {}", billing.workspaceId(), e.getMessage());
            }
        }
    }

    public void cancelBilling(Long memberId) {
        Workspace workspace = workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        workspaceBillingRepository.findByWorkspaceId(workspace.id())
                .ifPresent(b -> workspaceBillingRepository.cancel(b.id()));
        workspaceRepository.updatePlan(workspace.id(), WorkspacePlan.FREE);
    }
}
