package com.recyclingprojectbackend.pickup_request.model;

import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import com.recyclingprojectbackend.user.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "pickup_requests")
public class PickupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
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
    private Instant createdAt;

    private Instant acceptedAt;
    private Instant expiresAt;
    private Instant completedAt;
    private Instant ownerConfirmedAt;
    private Instant requesterConfirmedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = PickupStatus.PENDING;
    }

    public PickupRequest(long id, Item item, User requester, User owner, PickupStatus status, Instant createdAt, Instant acceptedAt, Instant expiresAt, Instant completedAt,  Instant ownerConfirmedAt, Instant requesterConfirmedAt) {
        this.id = id;
        this.item = item;
        this.requester = requester;
        this.owner = owner;
        this.status = status;
        this.createdAt = createdAt;
        this.acceptedAt = acceptedAt;
        this.expiresAt = expiresAt;
        this.completedAt = completedAt;
        this.ownerConfirmedAt = ownerConfirmedAt;
        this.requesterConfirmedAt = requesterConfirmedAt;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getOwnerConfirmedAt() {
        return ownerConfirmedAt;
    }

    public void setOwnerConfirmedAt(Instant ownerConfirmedAt) {
        this.ownerConfirmedAt = ownerConfirmedAt;
    }

    public Instant getRequesterConfirmedAt() {
        return requesterConfirmedAt;
    }

    public void setRequesterConfirmedAt(Instant requesterConfirmedAt) {
        this.requesterConfirmedAt = requesterConfirmedAt;
    }
}
