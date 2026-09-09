package com.example.backend.dto.payment;

/* Details the client needs to open the Razorpay checkout widget. */
import java.math.BigDecimal;

public record CreateOrderResponse(
    Long transactionId,
    String razorpayOrderId,
    String razorpayKeyId,
    BigDecimal amount,
    String currency
) {
}
