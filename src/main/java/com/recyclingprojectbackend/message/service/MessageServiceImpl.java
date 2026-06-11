package com.recyclingprojectbackend.message.service;

import com.recyclingprojectbackend.message.dto.ConversationDto;
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
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PickupRepository pickupRepository;
    private final MessageDtoMapper messageDtoMapper;
    private final NotificationService notificationService;

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, PickupRepository pickupRepository, MessageDtoMapper messageDtoMapper, NotificationService  notificationService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.pickupRepository = pickupRepository;
        this.messageDtoMapper = new MessageDtoMapper();
        this.notificationService = notificationService;
    }

    @Override
    public List<MessageDto> getMessagesForPickup(Long pickupId, Long userId) {
        PickupRequest request = getPickupAndVerifyAccess(pickupId, userId);

        return messageRepository.findByPickupRequest_IdOrderBySentAtAsc(request.getId())
                .stream()
                .map(messageDtoMapper::MessagetoMessageDto)
                .toList();
    }

    @Transactional
    @Override
    public MessageDto sendMessage(Long pickupId, Long senderId, String content) {
        PickupRequest request = getPickupAndVerifyAccess(pickupId, senderId);

        if (request.getStatus() != PickupStatus.ACCEPTED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Du kan kun sende beskeder på en accepteret afhentning"
            );
        }

        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Beskeden må ikke være tom"
            );
        }

        if (content.length() > 1000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Beskeden er for lang"
            );
        }

        User user = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brugeren blev ikke fundet"));

        Message message = new Message();
        message.setPickupRequest(request);
        message.setSender(user);
        message.setContent(content.trim());

        User recipient = user.getId().equals(request.getOwner().getId())
                ? request.getRequester()
                : request.getOwner();

        notificationService.createOrUpdateMessageNotification(
                recipient.getId(),
                user.getId(),
                user.getName(),
                pickupId
        );

        return messageDtoMapper.MessagetoMessageDto(messageRepository.save(message));
    }

    @Override
    public List<ConversationDto> findActiveChatsForUser(long userId) {
        List<PickupRequest> pickupRequests = pickupRepository.findActiveChatsForUser(userId);

        return pickupRequests.stream()
                .map(p -> {
                    boolean isOwner = p.getOwner().getId().equals(userId);
                    User otherUser = isOwner ? p.getRequester() : p.getOwner();

                    Message lastMessage = messageRepository.findFirstByPickupRequest_IdOrderBySentAtDesc(p.getId())
                            .orElse(null);

                    int unreadCount = messageRepository.countByPickupRequest_IdAndSender_IdNotAndReadAtIsNull(p.getId(), userId);

                    return new ConversationDto(
                            p.getId(),
                            otherUser.getId(),
                            otherUser.getName(),
                            otherUser.getImage(),
                            p.getItem().getName(),
                            p.getItem().getImage(),
                            lastMessage != null ? lastMessage.getContent() : null,
                            lastMessage != null ? lastMessage.getSentAt() : null,
                            unreadCount
                    );
                }).toList();
    }

    @Override
    public void markMessageAsRead(Long pickupId, Long userId) {
        PickupRequest request = getPickupAndVerifyAccess(pickupId, userId);

        List<Message> unreadMessages = messageRepository.findByPickupRequest_IdAndSender_IdNotAndReadAtIsNull(request.getId(), userId);

        if (unreadMessages.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        unreadMessages.forEach(unreadMessage -> {
            unreadMessage.setReadAt(now);
        });
        messageRepository.saveAll(unreadMessages);
    }

    private PickupRequest getPickupAndVerifyAccess(Long pickupId, Long userId) {
        PickupRequest pickup = pickupRepository.findById(pickupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anmodningen blev ikke fundet"));

        boolean isOwner = pickup.getOwner().getId().equals(userId);
        boolean isRequester = pickup.getRequester().getId().equals(userId);

        if (!isOwner && !isRequester) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du er ikke en del af denne anmodning");
        }
        return pickup;
    }
}
