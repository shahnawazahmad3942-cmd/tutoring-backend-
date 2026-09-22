package com.example.backend.service.booking;
/* Demo bookings are free but limited to one per user. */

import com.example.backend.entity.Slot;
import com.example.backend.entity.User;
import com.example.backend.enums.BookingStatus;
import com.example.backend.enums.BookingType;
import com.example.backend.exception.BusinessRuleException;
import com.example.backend.repository.BookingRepository;
import org.springframework.stereotype.Component;


@Component
public class DemoBookingPolicy implements BookingPolicy{

    private final BookingRepository bookingRepository;

    public DemoBookingPolicy(BookingRepository bookingRepository){
        this.bookingRepository = bookingRepository;
    }
    
    @Override
    public BookingType supportedBookingType() {
        return BookingType.DEMO;
    }

    @Override
    public int creditCost(){
        return 0;
    }

    @Override
    public void validateEligibility(User user, Slot slot){

        boolean alreadyUsed = bookingRepository.existsByUserIdAndTypeAndStatusNot(user.getId(), BookingType.DEMO, BookingStatus.CANCELLED);
        if(alreadyUsed){
            throw new BusinessRuleException("You have already used your demo booking");
        }
    }
}
