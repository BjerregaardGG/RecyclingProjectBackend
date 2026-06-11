package com.recyclingprojectbackend.message.controller;

import com.recyclingprojectbackend.message.dto.MessageDto;
import com.recyclingprojectbackend.message.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessageWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageWebSocketController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    // Principal --> the logged-in user (Configured in JWT-auth)
    @MessageMapping("/chat/{pickupId}")
    public void handleMessage(@DestinationVariable Long pickupId, @Payload MessagePayload messageContent, Principal principal) {
        try {
            Long senderId = Long.parseLong(principal.getName());
            MessageDto savedMessage = messageService.sendMessage(pickupId, senderId, messageContent.content());

            messagingTemplate.convertAndSend("/topic/chat/" + pickupId, savedMessage);

        } catch (Exception e) {
            System.err.println("Fejl i handleMessage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public record MessagePayload(String content) {}

}
