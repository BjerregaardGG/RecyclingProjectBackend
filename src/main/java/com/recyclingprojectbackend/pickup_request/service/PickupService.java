package com.recyclingprojectbackend.pickup_request.service;

import com.recyclingprojectbackend.message.dto.ConversationDto;
import com.recyclingprojectbackend.pickup_request.dto.PickUpRequestDto;
import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PickupService {
    PickUpRequestDto GetPickUpRequestById(Long id);
    List<PickUpRequestDto> findMyIncomingPickupRequests(long ownerId);
    List<PickUpRequestDto> findMyOutgoingPickupRequests(long ownerId);
    List<PickUpRequestDto> findAcceptedPickupRequests(long owner_id, PickupStatus status);
    PickUpRequestDto acceptRequest(long requestId, long ownerId);
    PickUpRequestDto declineRequest(long requestId, long ownerId);
    PickUpRequestDto confirmRequest(long requestId, long userId);
    PickUpRequestDto createPickupRequest(Long itemId, long userId);
}
