package com.recyclingprojectbackend.pickup_request.dto;

import com.recyclingprojectbackend.pickup_request.util.PickupStatus;

import java.time.Instant;

public record PickUpRequestDto(
    Long id,
    Long itemId,
    String itemName,
    String itemImage,
    Long requesterId,
    String requesterName,
    Long ownerId,
    String ownerName,
    PickupStatus status,
    Instant createdAt,
    Instant expiresAt,
    String pickupAddress,
    Instant ownerConfirmedAt,
    Instant requesterConfirmedAt
){}
