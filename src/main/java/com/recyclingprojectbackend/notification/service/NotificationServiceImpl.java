package com.recyclingprojectbackend.notification.service;

import com.recyclingprojectbackend.notification.Repository.NotificationRepository;
import com.recyclingprojectbackend.notification.dto.NotificationDto;
import com.recyclingprojectbackend.notification.dto.NotificationDtoMapper;
import com.recyclingprojectbackend.notification.model.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationDtoMapper notificationDtoMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,  NotificationDtoMapper notificationDtoMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationDtoMapper = notificationDtoMapper;
    }

    @Override
    public List<NotificationDto> getLoggedInUserNotifications(long userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationDtoMapper::NotificationToDtoMapper)
                .toList();
    }

    @Override
    public long getUnreadCount(long userId) {
        return notificationRepository.countByUser_IdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }
}
