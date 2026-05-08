package com.recyclingprojectbackend.message.repository;

import com.recyclingprojectbackend.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByPickupRequest_IdOrderBySentAtAsc(Long pickupRequestId);
    Optional<Message> findFirstByPickupRequest_IdOrderBySentAtDesc(Long pickupRequestId);
    // count all messages in the chat that is not sent by "me" and is unread
    int countByPickupRequest_IdAndSender_IdNotAndReadAtIsNull(
            Long pickupRequestId,
            Long senderId
    );
    List<Message> findByPickupRequest_IdAndSender_IdNotAndReadAtIsNull(
            Long pickupRequestId,
            Long senderId
    );

}
