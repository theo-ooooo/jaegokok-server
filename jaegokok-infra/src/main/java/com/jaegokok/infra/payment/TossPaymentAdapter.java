package com.jaegokok.infra.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaegokok.domain.payment.TossPaymentPort;
import com.jaegokok.infra.config.TossProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentAdapter implements TossPaymentPort {

    private static final String TOSS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String TOSS_BILLING_AUTH_URL = "https://api.tosspayments.com/v1/billing/authorizations/issue";
    private static final String TOSS_BILLING_CHARGE_URL = "https://api.tosspayments.com/v1/billing/";

    private final TossProperties tossProperties;
    private final ObjectMapper objectMapper;

    @Override
    public TossConfirmResult confirm(String paymentKey, String orderId, int amount) {
        String encoded = encodeSecretKey();

        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );

        try {
            RestClient restClient = RestClient.create();
            String responseBody = restClient.post()
                    .uri(TOSS_CONFIRM_URL)
                    .header("Authorization", "Basic " + encoded)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return new TossConfirmResult(true, "DONE", responseBody);
        } catch (RestClientException e) {
            log.error("Toss Payments confirm failed: paymentKey={}, orderId={}", paymentKey, orderId, e);
            return new TossConfirmResult(false, "FAILED", e.getMessage());
        }
    }

    @Override
    public BillingKeyResult issueBillingKey(String authKey, String customerKey) {
        String encoded = encodeSecretKey();

        Map<String, Object> body = Map.of(
                "authKey", authKey,
                "customerKey", customerKey
        );

        try {
            RestClient restClient = RestClient.create();
            String responseBody = restClient.post()
                    .uri(TOSS_BILLING_AUTH_URL)
                    .header("Authorization", "Basic " + encoded)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode json = objectMapper.readTree(responseBody);
            String billingKey = json.path("billingKey").asText();
            return new BillingKeyResult(true, billingKey, responseBody);
        } catch (Exception e) {
            log.error("Toss Payments billing key issuance failed: authKey={}, customerKey={}", authKey, customerKey, e);
            return new BillingKeyResult(false, null, e.getMessage());
        }
    }

    @Override
    public TossConfirmResult chargeWithBillingKey(String billingKey, String orderId, String orderName, int amount, String customerKey) {
        String encoded = encodeSecretKey();

        Map<String, Object> body = Map.of(
                "amount", amount,
                "orderId", orderId,
                "orderName", orderName,
                "customerKey", customerKey,
                "currency", "KRW"
        );

        try {
            RestClient restClient = RestClient.create();
            String responseBody = restClient.post()
                    .uri(TOSS_BILLING_CHARGE_URL + billingKey)
                    .header("Authorization", "Basic " + encoded)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return new TossConfirmResult(true, "DONE", responseBody);
        } catch (RestClientException e) {
            log.error("Toss Payments billing charge failed: billingKey={}, orderId={}", billingKey, orderId, e);
            return new TossConfirmResult(false, "FAILED", e.getMessage());
        }
    }

    private String encodeSecretKey() {
        String credentials = tossProperties.secretKey() + ":";
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
