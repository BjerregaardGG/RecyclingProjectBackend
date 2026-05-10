package com.recyclingprojectbackend.message.service;

import com.recyclingprojectbackend.message.dto.ConversationDto;
import com.recyclingprojectbackend.message.dto.MessageDto;
import com.recyclingprojectbackend.message.dto.MessageDtoMapper;
import com.recyclingprojectbackend.message.model.Message;
import com.recyclingprojectbackend.message.repository.MessageRepository;
import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.repository.PickupRepository;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PickupRepository pickupRepository;
    private final MessageDtoMapper messageDtoMapper;

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, PickupRepository pickupRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.pickupRepository = pickupRepository;
        this.messageDtoMapper = new MessageDtoMapper();
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
                throw new IllegalStateException("Can not send messages on a non-accepted pickup request");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content or content is null or empty");
        }

        if (content.length() > 1000) {
            throw new IllegalArgumentException("Content length exceeds 1000");
        }

        User user = userRepository.findById(senderId)
                .orElseThrow(() -> new AccessDeniedException("User not found"));

        Message message = new Message();
        message.setPickupRequest(request);
        message.setSender(user);
        message.setContent(content.trim());

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
                .orElseThrow(() -> new RuntimeException("Pickup not found"));

        boolean isOwner = pickup.getOwner().getId().equals(userId);
        boolean isRequester = pickup.getRequester().getId().equals(userId);

        if (!isOwner && !isRequester) {
            throw new AccessDeniedException("You are not part of this pickup");
        }
        return pickup;
    }


}
