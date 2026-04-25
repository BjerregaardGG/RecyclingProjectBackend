package com.recyclingprojectbackend.item.dto;

// ItemDto - Used for getting item objects from backend
public record ItemDto(
    long id,
    String name,
    String description,
    String secondDescription,
    String image,
    String category,
    Long userId,
    Double latitude,
    Double longitude
){}
