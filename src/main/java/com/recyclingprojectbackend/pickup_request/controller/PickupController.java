package com.recyclingprojectbackend.pickup_request.controller;

import com.recyclingprojectbackend.pickup_request.dto.PickUpRequestDto;
import com.recyclingprojectbackend.pickup_request.service.PickupService;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import com.recyclingprojectbackend.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pickups")
public class PickupController {

    private final PickupService pickupService;

    public PickupController(PickupService pickupService) {
        this.pickupService = pickupService;
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<PickUpRequestDto>> getIncomingPickupRequests(@AuthenticationPrincipal UserDto user){
        return ResponseEntity.ok(pickupService.findMyIncomingPickupRequests(user.id(), PickupStatus.PENDING));
    }

    @PatchMapping("/{requestId}/accept")
    public ResponseEntity<PickUpRequestDto> acceptRequest(@PathVariable long requestId, @AuthenticationPrincipal UserDto user){
        return ResponseEntity.ok(pickupService.acceptRequest(requestId, user.id()));
    }

    @PatchMapping("/{requestId}/decline")
    public ResponseEntity<PickUpRequestDto> declineRequest(@PathVariable long requestId, @AuthenticationPrincipal UserDto user) {
        return ResponseEntity.ok(pickupService.declineRequest(requestId, user.id()));
    }

    @PostMapping("/items/{itemId}")
    public ResponseEntity<PickUpRequestDto> addPickupRequest(@PathVariable long itemId, @AuthenticationPrincipal UserDto user) {
        return ResponseEntity.ok(pickupService.createPickupRequest(itemId, user.id()));
    }
}
