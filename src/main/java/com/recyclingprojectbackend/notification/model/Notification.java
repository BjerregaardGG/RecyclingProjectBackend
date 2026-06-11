package com.recyclingprojectbackend.notification.model;

import com.recyclingprojectbackend.notification.util.NotificationType;
import com.recyclingprojectbackend.user.model.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "other_user_id")
    private User otherUser;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    private String message;
    private Long relatedId;
    private boolean isRead = false;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Notification(long id, User user, User otherUser, NotificationType type, String message, Long relatedId, boolean isRead, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.otherUser = otherUser;
        this.type = type;
        this.message = message;
        this.relatedId = relatedId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Notification() {}

    public long getId() {
        return id;
    }

    public User getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
