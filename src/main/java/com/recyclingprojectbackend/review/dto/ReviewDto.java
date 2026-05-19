package com.recyclingprojectbackend.review.dto;

import java.time.Instant;

public record ReviewDto(
        long id,
        long reviewerId,
        String reviewerName,
        String reviewerImage,
        long reviewedId,
        int rating,
        Instant createdAt
) { }
