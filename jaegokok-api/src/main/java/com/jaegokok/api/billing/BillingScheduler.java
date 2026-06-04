package com.jaegokok.api.billing;

import com.jaegokok.domain.billing.BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingScheduler {

    private final BillingService billingService;

    @Scheduled(cron = "0 0 9 * * *")
    public void runDailyBilling() {
        log.info("정기결제 스케줄러 실행");
        billingService.processDueBillings();
    }
}
