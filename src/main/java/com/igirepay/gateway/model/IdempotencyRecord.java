package com.igirepay.gateway.model;

import com.igirepay.gateway.dto.PaymentResponse;

public class IdempotencyRecord {
    private String requestHash;
    private PaymentResponse response;
    private long createdAt;

    public IdempotencyRecord() {}

    public IdempotencyRecord(String requestHash, PaymentResponse response, long createdAt) {
        this.requestHash = requestHash;
        this.response = response;
        this.createdAt = createdAt;
    }

    public String getRequestHash() { return requestHash; }
    public void setRequestHash(String requestHash) { this.requestHash = requestHash; }

    public PaymentResponse getResponse() { return response; }
    public void setResponse(PaymentResponse response) { this.response = response; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}