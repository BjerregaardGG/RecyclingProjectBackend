package com.recyclingprojectbackend.pickup_request.model;

import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import com.recyclingprojectbackend.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pickup_requests")
public class PickupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PickupStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime acceptedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime completedAt;
    private LocalDateTime ownerConfirmedAt;
    private LocalDateTime requesterConfirmedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = PickupStatus.PENDING;
    }

    public PickupRequest(long id, Item item, User requester, User owner, PickupStatus status, LocalDateTime createdAt, LocalDateTime acceptedAt, LocalDateTime expiresAt, LocalDateTime completedAt) {
        this.id = id;
        this.item = item;
        this.requester = requester;
        this.owner = owner;
        this.status = status;
        this.createdAt = createdAt;
        this.acceptedAt = acceptedAt;
        this.expiresAt = expiresAt;
        this.completedAt = completedAt;
    }

    public PickupRequest() {}

    public boolean isFullyConfirmed() {
        return ownerConfirmedAt != null && requesterConfirmedAt != null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public PickupStatus getStatus() {
        return status;
    }

    public void setStatus(PickupStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getOwnerConfirmedAt() {
        return ownerConfirmedAt;
    }

    public void setOwnerConfirmedAt(LocalDateTime ownerConfirmedAt) {
        this.ownerConfirmedAt = ownerConfirmedAt;
    }

    public LocalDateTime getRequesterConfirmedAt() {
        return requesterConfirmedAt;
    }

    public void setRequesterConfirmedAt(LocalDateTime requesterConfirmedAt) {
        this.requesterConfirmedAt = requesterConfirmedAt;
    }
}
