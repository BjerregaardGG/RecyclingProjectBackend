package com.recyclingprojectbackend.message.dto;

import java.time.LocalDateTime;

public record MessageDto(
        Long id,
        Long pickupRequestId,
        Long senderId,
        String senderName,
        String content,
        LocalDateTime sentAt
) {}
