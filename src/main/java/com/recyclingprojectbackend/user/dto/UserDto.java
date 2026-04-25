package com.recyclingprojectbackend.user.dto;

public record UserDto (
    Long id,
    String name,
    String email,
    String image,
    String city
){}
