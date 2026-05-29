package com.recyclingprojectbackend.auth.dto;

// Used as response for /api/auth/login
public record AuthResponseDto (
    String token,
    String email,
    Long userId
){}