package com.igirepay.gateway.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    public PaymentRequest() {}

    public PaymentRequest(Double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    @Override
    public String toString() {
        // This toString is used for hashing. Do not change it.
        return "PaymentRequest(amount=" + amount + ", currency=" + currency + ")";
    }
}