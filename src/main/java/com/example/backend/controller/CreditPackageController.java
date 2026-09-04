package com.example.backend.controller;
/* REST endpoints for credit packages. Reads are public; writes require ADMIN. */

import com.example.backend.dto.creditpackage.CreditPackageRequest;
import com.example.backend.dto.creditpackage.CreditPackageResponse;
import com.example.backend.service.CreditPackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class CreditPackageController {

    private final CreditPackageService creditPackageService;

    public CreditPackageController(CreditPackageService creditPackageService){
        this.creditPackageService = creditPackageService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CreditPackageResponse>> listAll(){
        return ResponseEntity.ok(creditPackageService.listAll());
    }

    @GetMapping
    public ResponseEntity<List<CreditPackageResponse>> listActive(){
        return ResponseEntity.ok(creditPackageService.listActive());
    }


    @GetMapping("/{id}")
    public ResponseEntity<CreditPackageResponse> getById(@PathVariable Long id){
        return ResponseEntity
        .status(HttpStatus.OK)
        .body(creditPackageService.getById(id));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreditPackageResponse> create(@Valid @RequestBody CreditPackageRequest request){
        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(creditPackageService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreditPackageResponse> update(@PathVariable Long id, @Valid @RequestBody CreditPackageRequest request){
        return ResponseEntity
        .status(HttpStatus.OK)
        .body(creditPackageService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deActivate(@PathVariable Long id){
        creditPackageService.deActivate(id);

        return ResponseEntity
        .noContent()
        .build();
    }




    
}
