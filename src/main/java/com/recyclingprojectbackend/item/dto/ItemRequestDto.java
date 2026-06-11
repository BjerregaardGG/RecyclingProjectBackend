package com.recyclingprojectbackend.item.dto;

// ItemRequestDto - used for posting item objects to backend
// Should not be sent to client, due to address
public record ItemRequestDto (
    String name,
    String description,
    String secondTitle,
    Long categoryId,
    String image,
    String address,
    String city,
    Double latitude,
    Double longitude
){}