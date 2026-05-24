package com.recyclingprojectbackend.review.service;

import com.recyclingprojectbackend.notification.Repository.NotificationRepository;
import com.recyclingprojectbackend.notification.service.NotificationService;
import com.recyclingprojectbackend.notification.util.NotificationType;
import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.repository.PickupRepository;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import com.recyclingprojectbackend.review.dto.ReviewDto;
import com.recyclingprojectbackend.review.dto.ReviewDtoMapper;
import com.recyclingprojectbackend.review.model.Review;
import com.recyclingprojectbackend.review.repository.ReviewRepository;
import com.recyclingprojectbackend.user.dto.UserRatingDto;
import com.recyclingprojectbackend.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private PickupRepository pickupRepository;
    @Mock private NotificationService notificationService;
    @Mock private NotificationRepository notificationRepository;

    private ReviewServiceImpl reviewService;
    private PickupRequest completedPickup;
    private User owner;
    private User requester;

    @BeforeEach
    void setUp() {
        ReviewDtoMapper reviewDtoMapper = new ReviewDtoMapper();

        reviewService = new ReviewServiceImpl(
                reviewRepository,
                pickupRepository,
                notificationService,
                reviewDtoMapper,
                notificationRepository
        );

        owner = new User();
        owner.setId(1L);
        owner.setName("owner");

        requester = new User();
        requester.setId(2L);
        requester.setName("requester");

        completedPickup = new PickupRequest();
        completedPickup.setId(100L);
        completedPickup.setOwner(owner);
        completedPickup.setRequester(requester);
        completedPickup.setStatus(PickupStatus.COMPLETED);
    }

    @Test
    void createReview_validInput_savesReviewAndNotifiesUser() {
        // Arrange
        when(pickupRepository.findById(100L)).thenReturn(Optional.of(completedPickup));
        when(reviewRepository.existsByReviewer_IdAndPickup_Id(2L, 100L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(50L);
            return r;
        });

        // Act
        ReviewDto dto = reviewService.createReview(100L, 2L, 5);

        // Assert
        assertEquals(5, dto.rating());
        assertEquals(2L, dto.reviewerId());
        assertEquals(1L, dto.reviewedId());
        verify(reviewRepository).save(any(Review.class));
        verify(notificationService).createNotification(
                eq(1L),
                eq(2L),
                eq(NotificationType.INCOMING_REVIEW),
                any(String.class),
                eq(50L)
        );
    }

    @Test
    void getReviewsForUser() {
        // Arrange
        Review review1 = new Review();
        review1.setId(10L);
        review1.setReviewer(requester);
        review1.setReviewed(owner);
        review1.setPickup(completedPickup);
        review1.setRating(5);
        review1.setCreatedAt(Instant.now());

        Review review2 = new Review();
        review2.setId(11L);
        review2.setReviewer(requester);
        review2.setReviewed(owner);
        review2.setPickup(completedPickup);
        review2.setRating(4);
        review2.setCreatedAt(Instant.now().minusSeconds(3600));

        when(reviewRepository.findByReviewed_IdOrderByCreatedAtDesc(1L)).thenReturn(List.of(review1, review2));

        // Act
        List <ReviewDto> reviewDtoList = reviewService.getReviewsForUser(1L);

        // Assert
        assertEquals(2, reviewDtoList.size(), "Expected another size");
        verify(reviewRepository).findByReviewed_IdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getUserRating() {
        // Arrange
        when(reviewRepository.findAverageRatingForUser(1L)).thenReturn(4.5);
        when(reviewRepository.countByReviewed_Id(1L)).thenReturn(3L);

        // Act
        UserRatingDto userRating = reviewService.getUserRating(1L);

        // Assert
        verify(reviewRepository).findAverageRatingForUser(1L);
        verify(reviewRepository).countByReviewed_Id(1L);
        assertEquals(4.5, userRating.averageRating(), "Expected average rating");
        assertEquals(3L, userRating.totalReviews(), "Expected total reviews");
    }
}