package com.igirepay.gateway.service;

import com.igirepay.gateway.dto.PaymentResponse;
import com.igirepay.gateway.model.IdempotencyRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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

    private final Map<String, IdempotencyRecord> storage = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<ResponseEntity<?>>> inflightRequests = new ConcurrentHashMap<>();

    // 10 minutes in milliseconds
    private static final long TTL_LIMIT = 10 * 60 * 1000;

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
        storage.put(key, new IdempotencyRecord(hash, response, System.currentTimeMillis()));
    }

    public Map<String, CompletableFuture<ResponseEntity<?>>> getInflightRequests() {
        return inflightRequests;
    }

    // Runs every 60 seconds
    @Scheduled(fixedRate = 60000)
    public void cleanExpiredRecords() {
        long now = System.currentTimeMillis();
        storage.entrySet().removeIf(entry -> (now - entry.getValue().getCreatedAt()) > TTL_LIMIT);
    }
}