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
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PickupRepository pickupRepository;
    private final NotificationService notificationService;
    private final ReviewDtoMapper reviewDtoMapper;
    private final NotificationRepository notificationRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, PickupRepository pickupRepository, NotificationService notificationService, ReviewDtoMapper reviewDtoMapper, NotificationRepository notificationRepository) {
        this.reviewRepository = reviewRepository;
        this.pickupRepository = pickupRepository;
        this.notificationService = notificationService;
        this.reviewDtoMapper = reviewDtoMapper;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    @Override
    public ReviewDto createReview(long pickupId, long userId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Rating skal være mellem 1 og 5"
            );
        }

        PickupRequest request = pickupRepository.findById(pickupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kan ikke finde igangværende afhenting"));

        if (request.getStatus() != PickupStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Du kan kun anmelde efter afhentning er gennemført");
        }

        User reviewer;
        User reviewed;

        if (Objects.equals(request.getOwner().getId(), userId)) {
            reviewer = request.getOwner();
            reviewed = request.getRequester();
        } else if (Objects.equals(request.getRequester().getId(), userId)) {
            reviewer = request.getRequester();
            reviewed = request.getOwner();
        } else {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Du er ikke en del af denne afhentning"
            );
        }

        if (reviewRepository.existsByReviewer_IdAndPickup_Id(userId, pickupId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Du har allerede anmeldt denne afhentning"
            );
        }

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setReviewed(reviewed);
        review.setPickup(request);
        review.setRating(rating);

        Review savedReview = reviewRepository.save(review);

        // We delete the NEW_REVIEW notification when a review has been given
        notificationRepository.deleteByUser_IdAndTypeAndRelatedId(userId, NotificationType.NEW_REVIEW, pickupId);

        notificationService.createNotification(

                reviewed.getId(),
                reviewer.getId(),
                NotificationType.INCOMING_REVIEW,
                reviewer.getName() + " har givet dig en anmeldelse",
                savedReview.getId()
        );

        return reviewDtoMapper.reviewToReviewDto(savedReview);
    }

    @Override
    public List<ReviewDto> getReviewsForUser(long reviewedId) {
        return reviewRepository.findByReviewed_IdOrderByCreatedAtDesc(reviewedId)
                .stream()
                .map(reviewDtoMapper::reviewToReviewDto)
                .toList();
    }

    @Override
    public UserRatingDto getUserRating(long reviewedId) {
        Double average = reviewRepository.findAverageRatingForUser(reviewedId);
        long reviewCount = reviewRepository.countByReviewed_Id(reviewedId);
        return new UserRatingDto(average != null ? average : 0.0, reviewCount);
    }
}
