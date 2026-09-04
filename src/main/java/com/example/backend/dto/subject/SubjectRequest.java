package com.example.backend.dto.subject;

/* Incoming payload for creating or updating a subject. */

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectRequest (
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name should not exceed 255 character")
    String name,

    @Size(max = 1000, message = "Description should not exceed 1000 character")
    String description
){
    
}
