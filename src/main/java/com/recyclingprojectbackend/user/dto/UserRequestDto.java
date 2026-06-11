package com.recyclingprojectbackend.user.dto;

public record UserRequestDto(
        String name,
        String city,
        String postalCode,
        String profileText
) {}
