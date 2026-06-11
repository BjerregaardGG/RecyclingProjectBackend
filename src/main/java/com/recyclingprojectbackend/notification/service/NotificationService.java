package com.recyclingprojectbackend.notification.service;

import com.recyclingprojectbackend.notification.dto.NotificationDto;
import com.recyclingprojectbackend.notification.util.NotificationType;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getLoggedInUserNotifications(long userId);
    void markAllAsRead(long userId);
    // this is used in all related service layers
    void createNotification(long userId, long otherUserId, NotificationType type, String message, Long relatedId);
    void createOrUpdateMessageNotification(long recipientId, long senderId, String senderName, long pickupId);
}
