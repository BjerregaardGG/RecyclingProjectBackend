package com.recyclingprojectbackend.review.service;

import com.recyclingprojectbackend.review.dto.ReviewDto;
import com.recyclingprojectbackend.user.dto.UserRatingDto;

import java.util.List;

public interface ReviewService {
    ReviewDto createReview(long pickupId, long userId, int rating);
    List<ReviewDto> getReviewsForUser(long reviewedId);
    UserRatingDto getUserRating(long reviewedId);
}
