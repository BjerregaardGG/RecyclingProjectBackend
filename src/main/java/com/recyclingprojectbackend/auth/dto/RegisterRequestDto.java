package com.recyclingprojectbackend.auth.dto;

public record RegisterRequestDto (
    String email,
    String name,
    String password,
    String city,
    String postalCode
){}