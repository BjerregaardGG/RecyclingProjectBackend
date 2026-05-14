package com.recyclingprojectbackend.notification.dto;

import com.recyclingprojectbackend.notification.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationDtoMapper {

    public NotificationDto NotificationToDtoMapper(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getUser(), notification.getRequestState(), notification.getMessage(), notification.getRelatedId(), notification.isRead(), notification.getCreatedAt());
    }
}
