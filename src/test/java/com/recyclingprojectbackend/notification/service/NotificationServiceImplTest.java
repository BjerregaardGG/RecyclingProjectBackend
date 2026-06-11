package com.recyclingprojectbackend.notification.service;

import com.recyclingprojectbackend.notification.Repository.NotificationRepository;
import com.recyclingprojectbackend.notification.dto.NotificationDtoMapper;
import com.recyclingprojectbackend.notification.model.Notification;
import com.recyclingprojectbackend.notification.util.NotificationType;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;

    private NotificationServiceImpl notificationService;

    private User user;
    private User otherUser;

    @BeforeEach
    void setUp() {
        NotificationDtoMapper notificationDtoMapper = new NotificationDtoMapper();
        notificationService = new NotificationServiceImpl(
                notificationRepository,
                notificationDtoMapper,
                userRepository
        );

        user = new User();
        user.setId(1L);
        user.setName("Lasse");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Emilie");
    }


    @Test
    void createNotification() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));

        // Act
        notificationService.createNotification(
                1L,
                2L,
                NotificationType.REQUEST_ACCEPTED,
                "Din anmodning blev accepteret",
                100L
        );

        // Assert
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals(user, saved.getUser());
        assertEquals(otherUser, saved.getOtherUser());
        assertEquals(NotificationType.REQUEST_ACCEPTED, saved.getType());
        assertEquals("Din anmodning blev accepteret", saved.getMessage());
        assertEquals(100L, saved.getRelatedId());
    }

    @Test
    void updateMessageNotification() {
        // Arrange
        Notification existing = new Notification();
        existing.setId(50L);
        existing.setUser(user);
        existing.setOtherUser(otherUser);
        existing.setType(NotificationType.NEW_MESSAGE);
        existing.setMessage("Emilie har sendt en besked");
        existing.setCreatedAt(Instant.now().minusSeconds(120));

        when(notificationRepository.findFirstByUser_IdAndTypeAndRelatedIdAndIsReadFalse(
                1L, NotificationType.NEW_MESSAGE, 100L
        )).thenReturn(Optional.of(existing));

        // Act
        notificationService.createOrUpdateMessageNotification(1L, 2L, "Emilie", 100L);

        // Assert
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals(50L, saved.getId());
        assertEquals("Emilie har sendt flere beskeder", saved.getMessage());

        verify(userRepository, never()).findById(any());
    }
}