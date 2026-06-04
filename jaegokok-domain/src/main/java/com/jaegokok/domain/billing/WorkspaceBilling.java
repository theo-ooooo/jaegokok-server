package com.jaegokok.domain.billing;

import java.time.LocalDate;

public record WorkspaceBilling(
        Long id,
        Long workspaceId,
        String billingKey,
        String customerKey,
        String planKey,
        String status,
        LocalDate nextBillingDate
) {}
