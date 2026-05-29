package com.recyclingprojectbackend.notification.controller;

import com.recyclingprojectbackend.notification.dto.NotificationDto;
import com.recyclingprojectbackend.notification.service.NotificationService;
import com.recyclingprojectbackend.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificationDto>> GetLoggedInUserNotifications(@AuthenticationPrincipal UserDto user) {
        return ResponseEntity.ok(notificationService.getLoggedInUserNotifications(user.id()));
    }

    @PatchMapping("/me/read")
    public ResponseEntity<String> ReadNotifications(@AuthenticationPrincipal UserDto user) {
        notificationService.markAllAsRead(user.id());
        return ResponseEntity.ok().body("Notifications marked as read");
    }
}
