package com.recyclingprojectbackend.message.repository;

import com.recyclingprojectbackend.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByPickupRequest_IdOrderBySentAtAsc(Long pickupRequestId);
}
