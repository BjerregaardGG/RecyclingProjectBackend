package com.recyclingprojectbackend.review.dto;

import com.recyclingprojectbackend.review.model.Review;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Component;

@Component
public class ReviewDtoMapper {

    public ReviewDto reviewToReviewDto(Review review) {
        return new ReviewDto(
                review.getId(),
                review.getReviewer().getId(),
                review.getReviewer().getName(),
                review.getReviewer().getImage(),
                review.getReviewed().getId(),
                review.getRating(),
                review.getCreatedAt()
        );
    }
}
