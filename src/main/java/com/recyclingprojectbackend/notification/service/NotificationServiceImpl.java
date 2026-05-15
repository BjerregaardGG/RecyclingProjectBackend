package com.recyclingprojectbackend.notification.service;

import com.recyclingprojectbackend.notification.Repository.NotificationRepository;
import com.recyclingprojectbackend.notification.dto.NotificationDto;
import com.recyclingprojectbackend.notification.dto.NotificationDtoMapper;
import com.recyclingprojectbackend.notification.model.Notification;
import com.recyclingprojectbackend.notification.util.NotificationType;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationDtoMapper notificationDtoMapper;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,  NotificationDtoMapper notificationDtoMapper, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationDtoMapper = notificationDtoMapper;
        this.userRepository = userRepository;
    }

    @Override
    public List<NotificationDto> getLoggedInUserNotifications(long userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationDtoMapper::notificationToDtoMapper)
                .toList();
    }

    @Override
    public long getUnreadCount(long userId) {
        return notificationRepository.countByUser_IdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    @Transactional
    @Override
    public void createNotification(long userId, long otherUserId,  NotificationType type, String message, Long relatedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setOtherUser(otherUser);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedId(relatedId);

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void createOrUpdateMessageNotification(long recipientId, long senderId, String senderName, long pickupId) {
        Optional<Notification> existing = notificationRepository
                .findFirstByUser_IdAndTypeAndRelatedIdAndIsReadFalse(
                        recipientId,
                        NotificationType.NEW_MESSAGE,
                        pickupId
                );

        // Update existing
        if (existing.isPresent()) {
            Notification n = existing.get();
            n.setMessage(senderName + " har sendt flere beskeder");
            n.setCreatedAt(Instant.now());
            notificationRepository.save(n);
        } else {
            // Create a new
            createNotification(
                    recipientId,
                    senderId,
                    NotificationType.NEW_MESSAGE,
                    senderName + " har sendt en besked",
                    pickupId
            );
        }
    }
}
