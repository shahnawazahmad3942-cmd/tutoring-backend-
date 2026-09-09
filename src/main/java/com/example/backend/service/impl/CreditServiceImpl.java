package com.example.backend.service.impl;

/* Implements credit reads and ledger writes. Never updates an existing entry:
every movement is a new signed row, and the balance is their sum. */

import com.example.backend.dto.credit.LedgerEntryResponse;
import com.example.backend.entity.Booking;
import com.example.backend.entity.CreditLedger;
import com.example.backend.entity.Transaction;
import com.example.backend.entity.User;
import com.example.backend.enums.LedgerEntryType;
import com.example.backend.repository.CreditLedgerRepository;
import com.example.backend.service.CreditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreditServiceImpl implements CreditService {

    private final CreditLedgerRepository creditLedgerRepository;

    public CreditServiceImpl(CreditLedgerRepository creditLedgerRepository){
        this.creditLedgerRepository = creditLedgerRepository;
    }

    private LedgerEntryResponse toResponse(CreditLedger entry){

        return new LedgerEntryResponse(
            entry.getId(),
            entry.getDelta(),
            entry.getEntryType(),
            entry.getTransaction() != null ? entry.getTransaction().getId() : null,
            entry.getBooking() != null ? entry.getBooking().getId() : null,
            entry.getCreatedAt()
        );
    }

       
    @Override
    @Transactional(readOnly = true)
    public int getBalance(Long userId){
        return creditLedgerRepository.findBalanceByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryResponse> getHistory(Long userId){
        return creditLedgerRepository
               .findByUserIdOrderByCreatedAtDesc(userId)
               .stream()
               .map(this::toResponse)
               .toList();
    }

    @Override
    @Transactional
    public void recordTransactionEntry(User user, Transaction transaction, LedgerEntryType type, int delta){
        CreditLedger entry = new CreditLedger();
        entry.setUser(user);
        entry.setTransaction(transaction);
        entry.setEntryType(type);
        entry.setDelta(delta);

        creditLedgerRepository.save(entry);
    }

    @Override
    @Transactional
    public void recordBookingEntry(User user, Booking booking, LedgerEntryType type, int delta){
        CreditLedger entry = new CreditLedger();
        entry.setUser(user);
        entry.setBooking(booking);
        entry.setEntryType(type);
        entry.setDelta(delta);

        creditLedgerRepository.save(entry);
    }
}
