package com.recyclingprojectbackend.pickup_request.service;

import com.recyclingprojectbackend.pickup_request.dto.PickUpRequestDto;
import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PickupService {
    // Gets the pickupRequests on the Owner side
    List<PickUpRequestDto> findMyIncomingPickupRequests(long ownerId, PickupStatus status);
    PickUpRequestDto acceptRequest(long requestId, long ownerId);
    PickUpRequestDto declineRequest(long requestId, long ownerId);
    PickUpRequestDto createPickupRequest(Long itemId, long requesterId);
}
