package com.example.backend.dto.payment;
/* Incoming payload to start a credit package purchase. */

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest (@NotNull(message = "Package id is required") Long packageId){
    
}
