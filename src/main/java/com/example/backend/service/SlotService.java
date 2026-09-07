package com.example.backend.service;

/* Contract for slot management: admin scheduling plus student availability browsing. */

import com.example.backend.dto.slot.SlotRequest;
import com.example.backend.dto.slot.SlotResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface SlotService {

    SlotResponse create(SlotRequest request);

    SlotResponse update(Long id, SlotRequest request);

    SlotResponse getById(Long id);

    List<SlotResponse> listAvailable(Long subjectId, LocalDateTime from, LocalDateTime to);

    void cancel(Long id);
    
}
