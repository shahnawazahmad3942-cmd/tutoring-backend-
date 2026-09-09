package com.example.backend.dto.credit;
/* One credit ledger entry as returned by the API. */

import com.example.backend.enums.LedgerEntryType;
import java.time.LocalDateTime;

public record LedgerEntryResponse(
    Long id,
    int delta,
    LedgerEntryType entryType,
    Long transactionId,
    Long bookingId,
    LocalDateTime createdAt
) {
    
}
