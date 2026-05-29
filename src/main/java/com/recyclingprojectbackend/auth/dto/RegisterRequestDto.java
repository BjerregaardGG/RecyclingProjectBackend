package com.recyclingprojectbackend.auth.dto;

// Used as request for /api/auth/register
public record RegisterRequestDto (
    String email,
    String name,
    String password,
    String city,
    String postalCode
){}