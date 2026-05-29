package com.recyclingprojectbackend.review.dto;

// ReviewDtoRequest - used for receiving review objects to backend
public record ReviewDtoRequest(
        long pickupId,
        int rating
) {}
