package com.recyclingprojectbackend.review.dto;

// ReviewDtoRequest - used for posting review objects to backend
public record ReviewDtoRequest(
        long pickupId,
        int rating
) {}
