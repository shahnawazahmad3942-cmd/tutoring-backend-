package com.example.backend.repository;

import com.example.backend.entity.CreditLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CreditLedgerRepository extends JpaRepository<CreditLedger, Long>{

    @Query("SELECT COALESCE(SUM(l.delta), 0) FROM CreditLedger l WHERE l.user.id = :userId")
    int findBalanceByUserId(@Param("userId") Long userId);

    List<CreditLedger> findByUserIdOrderByCreatedAtDesc(Long userId);
    
}
