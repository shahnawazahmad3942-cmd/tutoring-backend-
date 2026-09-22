package com.example.backend.controller;

/* REST endpoints for bookings: students create and list their own,
admins mark a finished session complete. */

import com.example.backend.dto.booking.BookingRequest;
import com.example.backend.dto.booking.BookingResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/bookings") 
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService){
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    public ResponseEntity<BookingResponse> create(
        @AuthenticationPrincipal CustomUserDetails currentUser,
        @Valid @RequestBody BookingRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(bookingService.create(currentUser.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> listMine(
        @AuthenticationPrincipal CustomUserDetails currentUser
    ){
        return ResponseEntity.status(HttpStatus.OK)
                             .body(bookingService.listForUser(currentUser.getId()));
    }


    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.complete(id));
    }




    
}
