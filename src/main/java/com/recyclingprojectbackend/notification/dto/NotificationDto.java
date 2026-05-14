package com.recyclingprojectbackend.notification.dto;

import com.recyclingprojectbackend.user.model.User;
import java.time.Instant;

public record NotificationDto(
        long id,
        User user,
        String requestState,
        String message,
        long relatedId,
        boolean isRead,
        Instant createdAt
) {}
