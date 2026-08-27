package com.example.backend.repository;

import com.example.backend.entity.CreditPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditPackageRepository extends JpaRepository<CreditPackage, Long> {

    List<CreditPackage> findByActiveTrue();
}
