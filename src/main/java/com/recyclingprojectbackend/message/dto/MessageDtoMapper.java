package com.recyclingprojectbackend.message.dto;

import com.recyclingprojectbackend.message.model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageDtoMapper {

    public MessageDto MessagetoMessageDto(Message message){
        return new MessageDto(message.getId(), message.getPickupRequest().getId(), message.getSender().getId(), message.getSender().getName(), message.getContent(), message.getSentAt());
    }
}
