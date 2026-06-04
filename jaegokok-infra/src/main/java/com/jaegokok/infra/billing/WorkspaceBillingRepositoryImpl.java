package com.jaegokok.infra.billing;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.billing.WorkspaceBillingEntity;
import com.jaegokok.domain.billing.WorkspaceBilling;
import com.jaegokok.domain.billing.WorkspaceBillingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkspaceBillingRepositoryImpl implements WorkspaceBillingRepository {

    private final WorkspaceBillingJpaRepository workspaceBillingJpaRepository;

    @Override
    public WorkspaceBilling save(Long workspaceId, String billingKey, String customerKey, Long planId) {
        WorkspaceBillingEntity entity = WorkspaceBillingEntity.create(workspaceId, billingKey, customerKey, planId);
        return toBilling(workspaceBillingJpaRepository.save(entity));
    }

    @Override
    public Optional<WorkspaceBilling> findByWorkspaceId(Long workspaceId) {
        return workspaceBillingJpaRepository.findByWorkspaceId(workspaceId).map(this::toBilling);
    }

    @Override
    public List<WorkspaceBilling> findAllDueForBilling(LocalDate today) {
        return workspaceBillingJpaRepository.findAllDueForBilling(today).stream()
                .map(this::toBilling)
                .toList();
    }

    @Override
    public void cancel(Long id) {
        WorkspaceBillingEntity entity = workspaceBillingJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        entity.cancel();
    }

    @Override
    public void renewNextDate(Long id) {
        WorkspaceBillingEntity entity = workspaceBillingJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        entity.renewNextDate();
    }

    private WorkspaceBilling toBilling(WorkspaceBillingEntity e) {
        return new WorkspaceBilling(
                e.getId(),
                e.getWorkspaceId(),
                e.getBillingKey(),
                e.getCustomerKey(),
                e.getPlanId(),
                e.getStatus(),
                e.getNextBillingDate()
        );
    }
}
