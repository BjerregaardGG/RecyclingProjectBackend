package com.recyclingprojectbackend.notification.dto;
import java.time.Instant;

public record NotificationDto(
        long id,
        long otherUserId,
        String type,
        String message,
        long relatedId,
        boolean isRead,
        Instant createdAt
) {}
