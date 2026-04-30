package com.recyclingprojectbackend.pickup_request.repository;

import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PickupRepository extends JpaRepository<PickupRequest, Long> {

    List<PickupRequest> findByOwner_IdAndStatusIn(long owner_id, List<PickupStatus> statuses);
    List<PickupRequest> findByRequester_IdAndStatusIn(long requester_id, List<PickupStatus> statuses);
    Optional<PickupRequest> findByItem_idAndStatus(long item_id, PickupStatus status);
    List<PickupRequest> findByStatusAndExpiresAtBefore(PickupStatus status, LocalDateTime now);
    boolean existsByItem_IdAndRequester_IdAndStatusIn(
            Long itemId, Long requesterId, List<PickupStatus> statuses
    );
}
