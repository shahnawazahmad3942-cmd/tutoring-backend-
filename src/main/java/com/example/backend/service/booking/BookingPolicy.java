package com.example.backend.service.booking;
/* Strategy for one booking type: what it costs in credits and who may book it. */

import com.example.backend.entity.Slot;
import com.example.backend.entity.User;
import com.example.backend.enums.BookingType;

public interface BookingPolicy {

    BookingType supportedBookingType();

    int creditCost();

    void validateEligibility(User user, Slot slot);
    
}
