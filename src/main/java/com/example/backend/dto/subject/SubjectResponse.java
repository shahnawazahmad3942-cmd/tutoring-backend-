package com.example.backend.dto.subject;
/* Outgoing representation of a subject returned by the API. */

public record SubjectResponse(
    Long id,
    String name,
    String description,
    boolean active
) {
    
}
