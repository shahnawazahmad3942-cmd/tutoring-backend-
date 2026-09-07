package com.example.backend.controller;
/* REST endpoints for slots. Availability browsing is public; scheduling requires ADMIN. */

import com.example.backend.dto.slot.SlotRequest;
import com.example.backend.dto.slot.SlotResponse;
import com.example.backend.service.SlotService;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService){
        this.slotService = slotService;
    }


    @GetMapping
    public ResponseEntity<List<SlotResponse>> listAvailable(
        @RequestParam Long subjectId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ){
        return ResponseEntity
        .status(HttpStatus.OK)
        .body(slotService.listAvailable(subjectId, from, to));
    }


    @GetMapping("/{id}")
    public ResponseEntity<SlotResponse> gryById(@PathVariable Long id){

        return ResponseEntity
        .status(HttpStatus.OK)
        .body(slotService.getById(id));

    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SlotResponse> create(@Valid @RequestBody SlotRequest request){
        return ResponseEntity
        .status(HttpStatus.OK)
        .body(slotService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SlotResponse> update(@PathVariable Long id, @Valid @RequestBody SlotRequest request){
        return ResponseEntity
               .status(HttpStatus.OK)
               .body(slotService.update(id, request));
               
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancel(@PathVariable Long id){
        slotService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    
}
