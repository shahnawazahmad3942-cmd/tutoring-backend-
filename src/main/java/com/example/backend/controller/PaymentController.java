package com.example.backend.controller;

/* REST endpoints for the credit purchase flow: order creation for logged-in
students, and the public Razorpay webhook callback. */

import com.example.backend.dto.payment.CreateOrderRequest;
import com.example.backend.dto.payment.CreateOrderResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.PaymentService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }


    @PostMapping("/order")
    public ResponseEntity<CreateOrderResponse> createOrder(
        @AuthenticationPrincipal CustomUserDetails currentUser,
        @Valid @RequestBody CreateOrderRequest request
    ){
        return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(paymentService.createOrder(currentUser.getId(), request));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
        @RequestHeader("X-Razorpay-Signature") String signature,
        @RequestBody String payload
    ){
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
    
}
