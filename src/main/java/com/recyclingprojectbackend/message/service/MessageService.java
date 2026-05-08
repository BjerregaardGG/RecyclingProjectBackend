package com.recyclingprojectbackend.message.service;

import com.recyclingprojectbackend.message.dto.ConversationDto;
import com.recyclingprojectbackend.message.dto.MessageDto;

import java.util.List;

public interface MessageService {
    List<MessageDto> getMessagesForPickup(Long pickupId, Long userId);
    MessageDto sendMessage(Long pickupId, Long senderId, String content);
    List<ConversationDto> findActiveChatsForUser(long userId);
    void markMessageAsRead(Long pickupId, Long userId);
}
