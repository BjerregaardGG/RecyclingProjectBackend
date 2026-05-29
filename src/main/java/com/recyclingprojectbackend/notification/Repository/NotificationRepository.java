package com.recyclingprojectbackend.notification.Repository;

import com.recyclingprojectbackend.notification.model.Notification;
import com.recyclingprojectbackend.notification.util.NotificationType;
import com.recyclingprojectbackend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadForUser(@Param("userId") long userId);
    Optional<Notification> findFirstByUser_IdAndTypeAndRelatedIdAndIsReadFalse(long recipientId, NotificationType type, Long pickupId);
    @Modifying
    @Transactional
    void deleteByUser_IdAndTypeAndRelatedId(long userId, NotificationType type, Long relatedId);
}
