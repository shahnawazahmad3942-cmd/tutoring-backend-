package com.example.backend.service.booking;
/* Standard paid bookings: one credit each, no extra eligibility rules. */

import com.example.backend.entity.Slot;
import com.example.backend.entity.User;
import com.example.backend.enums.BookingType;
import org.springframework.stereotype.Component;

@Component
public class ActualBookingPolicy implements BookingPolicy {

    @Override
    public BookingType supportedBookingType() {
          return BookingType.ACTUAL;
    }


    @Override
    public int creditCost() {
        return 1;
    }

    @Override
    public void validateEligibility(User user, Slot slot){
        // No additional restrictions beyond having sufficient credits.
    }
    
}
