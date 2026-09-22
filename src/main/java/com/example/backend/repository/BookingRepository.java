package com.example.backend.repository;

import com.example.backend.entity.Booking;
import com.example.backend.enums.BookingStatus;
import com.example.backend.enums.BookingType;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    boolean existsBySlotId(Long slotId);

    boolean existsByUserIdAndTypeAndStatusNot(Long userId, BookingType type, BookingStatus status);
}

