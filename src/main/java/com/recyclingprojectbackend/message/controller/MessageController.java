package com.recyclingprojectbackend.message.controller;

import com.recyclingprojectbackend.message.dto.MessageDto;
import com.recyclingprojectbackend.message.service.MessageService;
import com.recyclingprojectbackend.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/pickup/{pickupId}")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable long pickupId, @AuthenticationPrincipal UserDto user) {
        return ResponseEntity.ok(messageService.getMessagesForPickup(pickupId, user.id()));
    }
}
