package com.recyclingprojectbackend.pickup_request.repository;

import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PickupRepository extends JpaRepository<PickupRequest, Long> {
    List<PickupRequest> findByOwner_IdAndStatusIn(long owner_id, List<PickupStatus> statuses);
    List<PickupRequest> findByRequester_IdAndStatusIn(long requester_id, List<PickupStatus> statuses);
    boolean existsByItem_IdAndRequester_IdAndStatusIn(
            Long itemId, Long requesterId, List<PickupStatus> statuses
    );
    @Query("""
    SELECT p FROM PickupRequest p
    WHERE (p.owner.id = :userId OR p.requester.id = :userId)
    AND p.status = 'ACCEPTED'
    ORDER BY p.acceptedAt DESC
""")
    List<PickupRequest> findActiveChatsForUser(@Param("userId") Long userId);
    List<PickupRequest> findByStatusAndExpiresAtBefore(
            PickupStatus status,
            Instant cutoff
    );
    List<PickupRequest> findByItem_IdAndStatusAndIdNot(
            long itemId,
            PickupStatus status,
            long excludeId
    );
    @Modifying
    @Transactional
    void deleteAllByItem_Id(long itemId);
}
