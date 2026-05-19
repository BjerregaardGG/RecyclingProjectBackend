package com.recyclingprojectbackend.review.repository;

import com.recyclingprojectbackend.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewed_IdOrderByCreatedAtDesc(long reviewedId);
    boolean existsByReviewer_IdAndPickup_Id(long reviewerId, long pickupId);
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewed.id = :userId")
    Double findAverageRatingForUser(@Param("userId") long userId);
    long countByReviewed_Id(long reviewedId);
}
