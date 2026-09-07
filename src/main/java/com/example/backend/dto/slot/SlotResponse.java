package com.example.backend.dto.slot;

/* Outgoing representation of a slot returned by the API. */

import com.example.backend.enums.SlotStatus;
import java.time.LocalDateTime;


public record SlotResponse(
    Long id,
    Long subjectId,
    String subjectName,
    LocalDateTime startTime,
    LocalDateTime endTime,
    SlotStatus status
) {
    
}
