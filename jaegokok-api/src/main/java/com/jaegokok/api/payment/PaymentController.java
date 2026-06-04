package com.jaegokok.api.payment;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.payment.BillingPayment;
import com.jaegokok.domain.payment.BillingPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final BillingPaymentService billingPaymentService;

    @PostMapping("/toss/confirm")
    public GlobalResponse<BillingPaymentResponse> confirmTossPayment(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TossConfirmRequest request
    ) {
        BillingPayment payment = billingPaymentService.confirmPayment(
                principal.getId(),
                request.paymentKey(),
                request.orderId(),
                request.amount(),
                request.planKey()
        );
        return GlobalResponse.success(HttpStatus.OK.value(), BillingPaymentResponse.from(payment));
    }
}
