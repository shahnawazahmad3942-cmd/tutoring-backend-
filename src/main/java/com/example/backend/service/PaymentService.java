package com.example.backend.service;

/* Contract for the credit purchase flow. */

import com.example.backend.dto.payment.CreateOrderRequest;
import com.example.backend.dto.payment.CreateOrderResponse;

public interface PaymentService {
    
    CreateOrderResponse createOrder(Long userId, CreateOrderRequest request);

    void handleWebhook(String payload, String signature);
}
