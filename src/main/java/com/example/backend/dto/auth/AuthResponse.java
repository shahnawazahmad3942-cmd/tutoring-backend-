package com.example.backend.dto.auth;

import com.example.backend.enums.Role;

public record AuthResponse(
    String token,
    Long userId,
    String name,
    String email,
    Role role
) {
    
}
