package com.example.backend.dto.booking;

/* Outgoing representation of a booking returned by the API. */

import com.example.backend.enums.BookingStatus;
import com.example.backend.enums.BookingType;

import java.time.LocalDateTime;

public record BookingResponse(
   Long id,
   Long slotId,
   String subjectName,
   LocalDateTime startTime,
   LocalDateTime endTime,
   BookingType type,
   BookingStatus status,
   LocalDateTime createdAt
) {}
