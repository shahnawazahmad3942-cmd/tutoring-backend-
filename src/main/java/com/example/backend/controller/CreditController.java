package com.example.backend.controller;
/* REST endpoints for the authenticated user's own credit balance and ledger history. */

import com.example.backend.dto.credit.CreditBalanceResponse;
import com.example.backend.dto.credit.LedgerEntryResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.CreditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/credits")
public class CreditController {

    private final CreditService creditService;

    public CreditController(CreditService creditService){
        this.creditService = creditService;
    }

    @GetMapping("/balance")
    public ResponseEntity<CreditBalanceResponse> getBalance(
        @AuthenticationPrincipal CustomUserDetails currentUser
    ){
        int balance = creditService.getBalance(currentUser.getId());
        return ResponseEntity
        .status(HttpStatus.OK)
        .body(new CreditBalanceResponse(currentUser.getId(), balance));
    }

    @GetMapping("/history")
    public ResponseEntity<List<LedgerEntryResponse>> getHistory(
        @AuthenticationPrincipal CustomUserDetails currentUser
    ){
        return ResponseEntity
               .status(HttpStatus.OK)
               .body(creditService.getHistory(currentUser.getId()));
    }
    
}
