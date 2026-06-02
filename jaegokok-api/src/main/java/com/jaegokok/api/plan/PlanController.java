package com.jaegokok.api.plan;

import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.subscription.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class PlanController {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @GetMapping
    public GlobalResponse<List<SubscriptionPlanResponse>> getPlans() {
        return GlobalResponse.success(HttpStatus.OK.value(),
                subscriptionPlanRepository.findAllActive().stream()
                        .map(SubscriptionPlanResponse::from)
                        .toList());
    }
}
