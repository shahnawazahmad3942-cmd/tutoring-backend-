package com.example.backend.dto.booking;
/* Incoming payload to book a slot. */

import com.example.backend.enums.BookingType;
import jakarta.validation.constraints.NotNull;

public record BookingRequest(
    @NotNull(message = "Slot id is required")
    Long slotId,

    @NotNull(message = "Booking type is required")
    BookingType type
) {
    
}
