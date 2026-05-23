package com.recyclingprojectbackend.review.service;

import com.recyclingprojectbackend.notification.Repository.NotificationRepository;
import com.recyclingprojectbackend.notification.service.NotificationService;
import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.repository.PickupRepository;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import com.recyclingprojectbackend.review.dto.ReviewDto;
import com.recyclingprojectbackend.review.dto.ReviewDtoMapper;
import com.recyclingprojectbackend.review.model.Review;
import com.recyclingprojectbackend.review.repository.ReviewRepository;
import com.recyclingprojectbackend.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private PickupRepository pickupRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ReviewDtoMapper reviewDtoMapper;
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private ReviewServiceImpl reviewServiceImpl;

    private User owner;
    private User requester;
    private PickupRequest completedPickup;

    @BeforeEach
    void setUp() {
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
    void createReview() {
        // Arrange
        when(pickupRepository.findById(100L)).thenReturn(Optional.of(completedPickup));
        when(reviewRepository.existsByReviewer_IdAndPickup_Id(2L, 100L)).thenReturn(Boolean.FALSE);
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        reviewServiceImpl.createReview(100L, 2L, 5);

        // Assert
        verify(reviewRepository).save(any(Review.class));
        verify(notificationService).createNotification(eq(1L), eq(2L), any(), any(), any());
    }

    @Test
    void getReviewsForUser() {
    }

    @Test
    void getUserRating() {
    }
}