package com.example.backend.dto.slot;
/* Incoming payload for creating or updating a slot. */

import com.example.backend.validation.ValidTimeRange;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@ValidTimeRange
public record SlotRequest(

    @NotNull(message = "Subject id is required")
    Long subjectId,

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    LocalDateTime startTime,

    @NotNull(message = "End time is required")
    LocalDateTime endTime
)
{
    
}
