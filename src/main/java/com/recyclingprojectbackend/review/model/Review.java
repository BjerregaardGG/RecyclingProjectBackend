package com.recyclingprojectbackend.review.model;

import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.user.model.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "review", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"reviewer_id", "pickup_id"})
})
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne
    @JoinColumn(name = "reviewed_id")
    private User reviewed;

    @ManyToOne
    @JoinColumn(name = "pickup_id")
    private PickupRequest pickup;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Review(long id, User reviewer, User reviewed, PickupRequest pickup, int rating, Instant createdAt) {
        this.id = id;
        this.reviewer = reviewer;
        this.reviewed = reviewed;
        this.pickup = pickup;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public Review() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public User getReviewed() {
        return reviewed;
    }

    public void setReviewed(User reviewed) {
        this.reviewed = reviewed;
    }

    public PickupRequest getPickup() {
        return pickup;
    }

    public void setPickup(PickupRequest pickup) {
        this.pickup = pickup;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
