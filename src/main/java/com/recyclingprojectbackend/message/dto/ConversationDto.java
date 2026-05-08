package com.recyclingprojectbackend.message.dto;

import java.time.LocalDateTime;

public record ConversationDto(
        Long pickupId,
        Long otherUserId,
        String otherUserName,
        String otherUserImage,
        String itemName,
        String itemImage,
        String lastMessageContent, 
        LocalDateTime lastMessageAt,
        int unreadCount
) {
}
