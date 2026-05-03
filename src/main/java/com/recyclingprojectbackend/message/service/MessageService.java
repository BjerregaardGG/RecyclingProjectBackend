package com.recyclingprojectbackend.message.service;

import com.recyclingprojectbackend.message.dto.MessageDto;

import java.util.List;

public interface MessageService {
    public List<MessageDto> getMessagesForPickup(Long pickupId, Long userId);
    public MessageDto sendMessage(Long pickupId, Long senderId, String content);


}
