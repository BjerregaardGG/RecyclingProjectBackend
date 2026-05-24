package com.recyclingprojectbackend.message.service;

import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.message.dto.MessageDto;
import com.recyclingprojectbackend.message.dto.MessageDtoMapper;
import com.recyclingprojectbackend.message.model.Message;
import com.recyclingprojectbackend.message.repository.MessageRepository;
import com.recyclingprojectbackend.notification.service.NotificationService;
import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.repository.PickupRepository;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PickupRepository pickupRepository;
    @Mock
    private NotificationService notificationService;

    private MessageServiceImpl messageService;

    private User sender;
    private User recipient;
    private PickupRequest pickupRequest;

    @BeforeEach
    void setUp() {
        MessageDtoMapper messageDtoMapper = new MessageDtoMapper();

        messageService = new MessageServiceImpl(
                messageRepository,
                userRepository,
                pickupRepository,
                messageDtoMapper,
                notificationService
        );

        sender = new User();
        sender.setId(1L);
        sender.setName("Allan");

        recipient = new User();
        recipient.setId(2L);
        recipient.setName("Jan");

        pickupRequest = new PickupRequest();
        pickupRequest.setId(100L);
        pickupRequest.setOwner(recipient);
        pickupRequest.setRequester(sender);
        pickupRequest.setStatus(PickupStatus.ACCEPTED);
        pickupRequest.setItem(new Item());

    }

    @Test
    void sendMessage() {
        // Arrange
        String content = "Kan jeg mødes med dig klokken 14?";

        Message message = new Message();
        message.setPickupRequest(pickupRequest);
        message.setSender(sender);
        message.setContent(content.trim());
        when(pickupRepository.findById(100L)).thenReturn(Optional.of(pickupRequest));
        when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        // Act
        MessageDto messageDto = messageService.sendMessage(pickupRequest.getId(), sender.getId(), content);

        // Assert
        verify(pickupRepository).findById(100L);
        verify(userRepository).findById(sender.getId());
        verify(messageRepository).save(any(Message.class));
        assertEquals(sender.getName(), messageDto.senderName());
        assertEquals(message.getContent(), messageDto.content());
        assertEquals(pickupRequest.getId(), messageDto.pickupRequestId());
    }

    @Test
    void findActiveChatsForUser() {
    }

    @Test
    void markMessageAsRead() {
    }
}