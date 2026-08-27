package com.example.backend.repository;

import com.example.backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{

    Optional<Transaction> findByRazorpayOrderId(String razorpayOrderId);

    List<Transaction> findByUserId(Long userId);
    
}
