package com.recyclingprojectbackend.auth.dto;

public record LoginRequestDto (
    String email,
    String password
){}