package com.example.backend.service;
/* Contract for credit package management: admin CRUD plus public listing. */
import com.example.backend.dto.creditpackage.CreditPackageRequest;
import com.example.backend.dto.creditpackage.CreditPackageResponse;

import java.util.List;

public interface CreditPackageService {

    CreditPackageResponse create(CreditPackageRequest request);

    CreditPackageResponse update(Long id, CreditPackageRequest request);

    CreditPackageResponse getById(Long id);

    List<CreditPackageResponse> listActive();

    List<CreditPackageResponse> listAll();
    
    void deActivate(Long id);
    
}
