package com.example.backend.service;
/* Contract for booking operations. */

import com.example.backend.dto.booking.BookingRequest;
import com.example.backend.dto.booking.BookingResponse;

import java.util.List;
public interface BookingService {

    BookingResponse create(Long userId, BookingRequest request);

    List<BookingResponse> listForUser(Long userId);

    BookingResponse complete(Long bookingId);


}


