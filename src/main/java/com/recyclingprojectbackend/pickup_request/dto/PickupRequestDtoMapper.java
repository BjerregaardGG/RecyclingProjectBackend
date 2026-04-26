package com.recyclingprojectbackend.pickup_request.dto;

import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import org.springframework.stereotype.Component;

@Component
public class PickupRequestDtoMapper {

    public PickUpRequestDto pickupRequestToPickupRequestDto(PickupRequest pickupRequest) {

        String address = pickupRequest.getStatus() == PickupStatus.ACCEPTED ?
                pickupRequest.getItem().getAddress() : null;

        return new PickUpRequestDto(
                pickupRequest.getId(),
                pickupRequest.getItem().getId(),
                pickupRequest.getItem().getName(),
                pickupRequest.getItem().getImage(),
                pickupRequest.getRequester().getId(),
                pickupRequest.getRequester().getName(),
                pickupRequest.getOwner().getId(),
                pickupRequest.getOwner().getName(),
                pickupRequest.getStatus(),
                pickupRequest.getCreatedAt(),
                pickupRequest.getExpiresAt(),
                address
        );
    }
}
