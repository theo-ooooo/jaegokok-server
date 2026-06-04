package com.jaegokok.domain.billing;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkspaceBillingRepository {
    WorkspaceBilling save(Long workspaceId, String billingKey, String customerKey, Long planId);
    WorkspaceBilling savePending(Long workspaceId, String billingKey, String customerKey, Long planId);
    void activate(Long id);
    Optional<WorkspaceBilling> findByWorkspaceId(Long workspaceId);
    List<WorkspaceBilling> findAllDueForBilling(LocalDate today);
    void cancel(Long id);
    void renewNextDate(Long id);
}
