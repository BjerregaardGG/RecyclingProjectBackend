package com.recyclingprojectbackend.pickup_request.repository;

import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PickupRepository extends JpaRepository<PickupRequest, Long> {

    List<PickupRequest> findByOwner_IdAndStatus(long owner_id, PickupStatus status);
    List<PickupRequest> findByRequester_Id(long requester_id);
    Optional<PickupRequest> findByItem_idAndStatus(long item_id, String status);
    List<PickupRequest> findByStatusAndExpiresAtBefore(PickupStatus status, LocalDateTime now);
    boolean existsByItem_IdAndRequester_IdAndStatusIn(
            Long itemId, Long requesterId, List<PickupStatus> statuses
    );
}
