package com.recyclingprojectbackend.message.controller;

import com.recyclingprojectbackend.message.dto.ConversationDto;
import com.recyclingprojectbackend.message.dto.MessageDto;
import com.recyclingprojectbackend.message.service.MessageService;
import com.recyclingprojectbackend.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.LongToIntFunction;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/conversations/me")
    public ResponseEntity<List<ConversationDto>> getAllConversations(@AuthenticationPrincipal UserDto user) {
        return ResponseEntity.ok(messageService.findActiveChatsForUser(user.id()));
    }

    @GetMapping("/pickup/{pickupId}")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable long pickupId, @AuthenticationPrincipal UserDto user) {
        return ResponseEntity.ok(messageService.getMessagesForPickup(pickupId, user.id()));
    }

    @PatchMapping("/pickup/{pickupId}/mark-as-read")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable long pickupId, @AuthenticationPrincipal UserDto user) {
        messageService.markMessageAsRead(pickupId, user.id());
        return ResponseEntity.noContent().build();
    }
}
