package com.igirepay.gateway.controller;

import com.igirepay.gateway.dto.ErrorResponse;
import com.igirepay.gateway.dto.PaymentRequest;
import com.igirepay.gateway.dto.PaymentResponse;
import com.igirepay.gateway.model.IdempotencyRecord;
import com.igirepay.gateway.service.IdempotencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class PaymentController {

    private final IdempotencyService idempotencyService;

    public PaymentController(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @PostMapping("/process-payment")
    public ResponseEntity<?> processPayment(
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey,
            @RequestBody PaymentRequest paymentRequest) throws ExecutionException, InterruptedException {


        if (paymentRequest.getAmount() == null || paymentRequest.getAmount() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("VALIDATION_ERROR", "Amount must be greater than 0"));
        }
        if (paymentRequest.getCurrency() == null || paymentRequest.getCurrency().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("VALIDATION_ERROR", "Currency is required"));
        }
        

        String currentRequestHash = idempotencyService.generateHash(paymentRequest);

     
        if (idempotencyService.isKeyPresent(idempotencyKey)) {
            IdempotencyRecord record = idempotencyService.getRecord(idempotencyKey);
            if (!record.getRequestHash().equals(currentRequestHash)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Idempotency key already used for a different request body.");
            }
            return ResponseEntity.ok().header("X-Cache-Hit", "true").body(record.getResponse());
        }

   
        CompletableFuture<ResponseEntity<?>> newFuture = new CompletableFuture<>();
        CompletableFuture<ResponseEntity<?>> existingFuture = idempotencyService.getInflightRequests()
                .putIfAbsent(idempotencyKey, newFuture);

        if (existingFuture != null) {
            return existingFuture.get();
        }

        try {
            
            Thread.sleep(2000);

            String message = "Charged " + paymentRequest.getAmount() + " " + paymentRequest.getCurrency();
            PaymentResponse response = new PaymentResponse(message);

            idempotencyService.saveRecord(idempotencyKey, currentRequestHash, response);

            ResponseEntity<PaymentResponse> finalResponse = ResponseEntity.ok(response);
            newFuture.complete(finalResponse);
            return finalResponse;

        } catch (Exception e) {
            newFuture.completeExceptionally(e);
            throw e;
        } finally {
            idempotencyService.getInflightRequests().remove(idempotencyKey);
        }
    }
}