package com.igirepay.gateway.dto;

public class PaymentResponse {
    private String message;

    public PaymentResponse() {}

    public PaymentResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}