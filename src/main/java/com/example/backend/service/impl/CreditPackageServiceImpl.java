package com.example.backend.service.impl;
/* Implements credit package management. Deletion is a soft delete so past
transactions referencing the package stay intact. */

import com.example.backend.dto.creditpackage.CreditPackageRequest;
import com.example.backend.dto.creditpackage.CreditPackageResponse;
import com.example.backend.entity.CreditPackage;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CreditPackageRepository;
import com.example.backend.service.CreditPackageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreditPackageServiceImpl implements CreditPackageService{

    private final CreditPackageRepository creditPackageRepository;

    public CreditPackageServiceImpl(CreditPackageRepository creditPackageRepository){
        this.creditPackageRepository = creditPackageRepository;
    }

    private CreditPackageResponse toResponse(CreditPackage creditPackage){
        return new CreditPackageResponse(
                creditPackage.getId(),
                creditPackage.getName(),
                creditPackage.getDescription(),
                creditPackage.getPrice(),
                creditPackage.getCredits(),
                creditPackage.isActive()
        );
    }


    @Override
    @Transactional
    public CreditPackageResponse create(CreditPackageRequest request){
        
        CreditPackage creditPackage = new CreditPackage();

        creditPackage.setName(request.name());
        creditPackage.setDescription(request.description());
        creditPackage.setPrice(request.price());
        creditPackage.setCredits(request.credits());
        creditPackage.setActive(true);

        return toResponse(creditPackageRepository.save(creditPackage));
    }

    @Override
    @Transactional
    public CreditPackageResponse update(Long id, CreditPackageRequest request){

        CreditPackage creditPackage = creditPackageRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("credit package not found")
        );

        creditPackage.setName(request.name());
        creditPackage.setDescription(request.description());
        creditPackage.setPrice(request.price());
        creditPackage.setCredits(request.credits());
        
        return toResponse(creditPackage);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPackageResponse getById(Long id){
        CreditPackage creditPackage = creditPackageRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("creditPackage not found")
        );
        return toResponse(creditPackage);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CreditPackageResponse> listActive(){

        return creditPackageRepository.findByActiveTrue()
        .stream()
        .map(this::toResponse)
        .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<CreditPackageResponse> listAll(){

        return creditPackageRepository.findAll()
        .stream()
        .map(this::toResponse)
        .toList();
    }


    @Override
    @Transactional
    public void deActivate(Long id){

        CreditPackage creditPackage = creditPackageRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("credit package not found")
        );
        creditPackage.setActive(false);
    }
}


