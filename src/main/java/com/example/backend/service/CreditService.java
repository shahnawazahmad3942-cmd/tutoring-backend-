package com.example.backend.service;

/* Contract for credit operations. Balance is always derived from the ledger;
all credit movements are recorded as new append-only entries. */

import com.example.backend.dto.credit.LedgerEntryResponse;
import com.example.backend.entity.Booking;
import com.example.backend.entity.Transaction;
import com.example.backend.entity.User;
import com.example.backend.enums.LedgerEntryType;

import java.util.List;

public interface CreditService {
    
    int getBalance(Long userId);

    List<LedgerEntryResponse> getHistory(Long userId);

    void recordTransactionEntry(User user, Transaction transaction, LedgerEntryType type, int delta);

    void recordBookingEntry(User user, Booking booking, LedgerEntryType type, int delta);
    
}
