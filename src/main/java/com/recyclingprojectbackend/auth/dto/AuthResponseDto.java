package com.recyclingprojectbackend.auth.dto;

public record AuthResponseDto (
    String token,
    String email,
    Long userId
){}