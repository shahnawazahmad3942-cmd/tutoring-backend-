package com.example.backend.service.impl;

/* Implements booking creation as a single atomic unit: the slot row is locked,
credits are held, the booking is written and the slot is marked BOOKED, all in
one transaction. Either every step commits or none does. */

import com.example.backend.dto.booking.BookingRequest;
import com.example.backend.dto.booking.BookingResponse;
import com.example.backend.entity.Booking;
import com.example.backend.entity.Slot;
import com.example.backend.entity.User;
import com.example.backend.enums.BookingStatus;
import com.example.backend.enums.LedgerEntryType;
import com.example.backend.enums.SlotStatus;
import com.example.backend.exception.BusinessRuleException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.BookingRepository;
import com.example.backend.repository.SlotRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.BookingService;
import com.example.backend.service.CreditService;
import com.example.backend.service.booking.BookingPolicy;
import com.example.backend.service.booking.BookingPolicyFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;


@Service
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;
    private final CreditService creditService;
    private final BookingPolicyFactory bookingPolicyFactory;


    public BookingServiceImpl(
        BookingRepository bookingRepository,
        SlotRepository slotRepository,
        UserRepository userRepository,
        CreditService creditService,
        BookingPolicyFactory bookingPolicyFactory
    ){
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
        this.creditService = creditService;
        this.bookingPolicyFactory = bookingPolicyFactory;
    }

    private BookingResponse toResponse(Booking booking){

        Slot slot = booking.getSlot();

        return  new BookingResponse(
            booking.getId(),
            slot.getId(),
            slot.getSubject().getName(),
            slot.getStartTime(),
            slot.getEndTime(),
            booking.getType(),
            booking.getStatus(),
            booking.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public BookingResponse create(Long userId, BookingRequest request){

        User user = userRepository.findById(userId).orElseThrow(
            () -> new ResourceNotFoundException("User not found")
        );
        
        Slot slot = slotRepository.findWithLockById(request.slotId()).orElseThrow(
            () -> new ResourceNotFoundException("Slot not found with id " + request.slotId())
        );

        if(slot.getStatus() != SlotStatus.AVAILABLE){
            throw new BusinessRuleException("This slot is no longer available");
        }

        if(slot.getStartTime().isBefore(LocalDateTime.now())){
            throw new BusinessRuleException("This slot has already started");
        }

        BookingPolicy policy = bookingPolicyFactory.resolve(request.type());
        policy.validateEligibility(user, slot);

        int cost = policy.creditCost();

        if(cost > 0 && creditService.getBalance(userId) < cost){
            throw new BusinessRuleException("Insufficient credits");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSlot(slot);
        booking.setType(request.type());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking saved = bookingRepository.save(booking);

        if(cost > 0){
            creditService.recordBookingEntry(user, saved, LedgerEntryType.HOLD, -cost);
        }

        slot.setStatus(SlotStatus.BOOKED);
        return toResponse(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> listForUser(Long userId){

        List<Booking> bookings = bookingRepository.findByUserId(userId);

        List<BookingResponse> list = new ArrayList<>();

        for(Booking itr : bookings){
            list.add(toResponse(itr));
        }

        return list;


    }

         @Override
    @Transactional
    public BookingResponse complete(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
            () -> new ResourceNotFoundException("Booking not found with id " + bookingId)
        );

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessRuleException("Only a confirmed booking can be completed");
        }

        Slot slot = booking.getSlot();

        if (slot.getEndTime().isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("This session has not finished yet");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        slot.setStatus(SlotStatus.COMPLETED);

        int cost = bookingPolicyFactory.resolve(booking.getType()).creditCost();

        if (cost > 0) {
            creditService.recordBookingEntry(booking.getUser(), booking, LedgerEntryType.CONSUME, 0);
        }

        return toResponse(booking);
    }
   

    
}
