package com.igirepay.gateway.controller;

import com.igirepay.gateway.dto.PaymentRequest;
import com.igirepay.gateway.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @PostMapping("/process-payment")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody PaymentRequest paymentRequest) {

        try {
            // Simulate processing delay of 2 seconds
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String message = "Charged " + paymentRequest.getAmount() + " " + paymentRequest.getCurrency();
        PaymentResponse response = new PaymentResponse(message);

        return ResponseEntity.ok(response);
    }
}