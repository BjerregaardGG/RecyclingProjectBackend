package com.recyclingprojectbackend.review.controller;

import com.recyclingprojectbackend.review.dto.ReviewDto;
import com.recyclingprojectbackend.review.dto.ReviewDtoRequest;
import com.recyclingprojectbackend.review.service.ReviewService;
import com.recyclingprojectbackend.user.dto.UserDto;
import com.recyclingprojectbackend.user.dto.UserRatingDto;
import com.recyclingprojectbackend.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable long userId) {
        return ResponseEntity.ok(reviewService.getReviewsForUser(userId));
    }

    @GetMapping("/user/{userId}/average")
    public ResponseEntity<UserRatingDto> getAverageReviews(@PathVariable long userId) {
        return ResponseEntity.ok(reviewService.getUserRating(userId));
    }

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDtoRequest req, @AuthenticationPrincipal UserDto user) {
        return ResponseEntity.ok(reviewService.createReview(req.pickupId(), user.id(), req.rating()));
    }
}
