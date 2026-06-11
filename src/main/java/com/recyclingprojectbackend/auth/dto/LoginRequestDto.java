package com.recyclingprojectbackend.auth.dto;

// Used as request for /api/auth/login
public record LoginRequestDto (
    String email,
    String password
){}