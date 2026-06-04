package com.jaegokok.domain.billing;

import java.time.LocalDate;

public record WorkspaceBilling(
        Long id,
        Long workspaceId,
        String billingKey,
        String customerKey,
        Long planId,
        String status,
        LocalDate nextBillingDate
) {}
