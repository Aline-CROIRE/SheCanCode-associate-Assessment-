package com.igirepay.gateway.model;

import com.igirepay.gateway.dto.PaymentResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdempotencyRecord {
    private String requestHash;
    private PaymentResponse response;
}