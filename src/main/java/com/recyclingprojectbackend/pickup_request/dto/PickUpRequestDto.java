package com.recyclingprojectbackend.pickup_request.dto;

import com.recyclingprojectbackend.pickup_request.util.PickupStatus;

import java.time.LocalDateTime;

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
    LocalDateTime createdAt,
    LocalDateTime expiresAt,
    String pickupAddress  // null indtil ACCEPTED
){}
