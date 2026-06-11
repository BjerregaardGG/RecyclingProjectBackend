package com.recyclingprojectbackend.user.dto;

public record UserRatingDto(
        double averageRating,
        long totalReviews
) {
}
