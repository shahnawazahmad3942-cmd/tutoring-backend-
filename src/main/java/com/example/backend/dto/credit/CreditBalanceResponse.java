package com.example.backend.dto.credit;
/* A user's current credit balance, computed from the ledger. */

public record CreditBalanceResponse(
    Long userId,
    int balance
) {}
