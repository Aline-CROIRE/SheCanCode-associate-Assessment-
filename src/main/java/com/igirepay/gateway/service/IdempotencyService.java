package com.igirepay.gateway.service;

import com.igirepay.gateway.dto.PaymentResponse;
import com.igirepay.gateway.model.IdempotencyRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyService {

    // Final storage for completed requests
    private final Map<String, IdempotencyRecord> storage = new ConcurrentHashMap<>();

    // Temporary storage for requests currently being processed
    private final Map<String, CompletableFuture<ResponseEntity<?>>> inflightRequests = new ConcurrentHashMap<>();

    public String generateHash(Object body) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String bodyString = body.toString();
            byte[] hash = digest.digest(bodyString.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    public boolean isKeyPresent(String key) {
        return storage.containsKey(key);
    }

    public IdempotencyRecord getRecord(String key) {
        return storage.get(key);
    }

    public void saveRecord(String key, String hash, PaymentResponse response) {
        storage.put(key, new IdempotencyRecord(hash, response));
    }

    public Map<String, CompletableFuture<ResponseEntity<?>>> getInflightRequests() {
        return inflightRequests;
    }
}