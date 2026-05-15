package com.recyclingprojectbackend.notification.dto;

import com.recyclingprojectbackend.notification.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationDtoMapper {

    public NotificationDto notificationToDtoMapper(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getOtherUser() != null ? notification.getOtherUser().getId() : null, notification.getType().toString(), notification.getMessage(), notification.getRelatedId(), notification.isRead(), notification.getCreatedAt());
    }
}
