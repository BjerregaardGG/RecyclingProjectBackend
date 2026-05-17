package com.recyclingprojectbackend.notification.util;

public enum NotificationType {
    PICKUP_REQUEST,       // relatedId = pickupId
    REQUEST_ACCEPTED,     // relatedId = pickupId
    REQUEST_DECLINED,     // relatedId = pickupId
    PICKUP_COMPLETED,     // relatedId = pickupId
    PICKUP_EXPIRED,       // relatedId = pickupId
    NEW_REVIEW,           // relatedId = reviewId
    NEW_MESSAGE,          // relatedId = pickupId (chat-tråden)
    ITEM_FAVORITED,       // relatedId = itemId
    SYSTEM,
}
