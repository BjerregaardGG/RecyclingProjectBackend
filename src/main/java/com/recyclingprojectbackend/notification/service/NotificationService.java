package com.recyclingprojectbackend.notification.service;

import com.recyclingprojectbackend.notification.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getLoggedInUserNotifications(long userId);
    long getUnreadCount(long userId);
    void markAllAsRead(long userId);
}
