package com.recyclingprojectbackend.pickup_request.service;

import com.recyclingprojectbackend.exceptions.BusinessException;
import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.repository.ItemRepository;
import com.recyclingprojectbackend.pickup_request.dto.PickUpRequestDto;
import com.recyclingprojectbackend.pickup_request.dto.PickupRequestDtoMapper;
import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.repository.PickupRepository;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PickupServiceImpl implements PickupService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PickupRepository pickupRepository;
    private final PickupRequestDtoMapper pickupRequestDtoMapper;

    public PickupServiceImpl(ItemRepository itemRepository, UserRepository userRepository,  PickupRepository pickupRepository, PickupRequestDtoMapper pickupRequestDtoMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.pickupRepository = pickupRepository;
        this.pickupRequestDtoMapper = pickupRequestDtoMapper;
    }

    @Override
    public PickUpRequestDto GetPickUpRequestById(Long id) {
        PickupRequest pickUp = pickupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pickup not found with id: " + id));

        return pickupRequestDtoMapper.pickupRequestToPickupRequestDto(pickUp);
    }

    @Override
    public List<PickUpRequestDto> findMyIncomingPickupRequests(long owner_id) {
        return pickupRepository.findByOwner_IdAndStatusIn(owner_id, List.of(PickupStatus.PENDING, PickupStatus.ACCEPTED, PickupStatus.COMPLETED))
                .stream()
                .map(pickupRequestDtoMapper::pickupRequestToPickupRequestDto)
                .toList();
    }

    @Override
    public List<PickUpRequestDto> findMyOutgoingPickupRequests(long ownerId) {
        return pickupRepository.findByRequester_IdAndStatusIn(ownerId, List.of(PickupStatus.PENDING, PickupStatus.ACCEPTED, PickupStatus.COMPLETED))
                .stream()
                .map(pickupRequestDtoMapper::pickupRequestToPickupRequestDto)
                .toList();
    }

    @Override
    public List<PickUpRequestDto> findAcceptedPickupRequests(long owner_id, PickupStatus status) {
        return pickupRepository.findByOwner_IdAndStatusIn(owner_id, List.of(PickupStatus.ACCEPTED))
                .stream()
                .map(pickupRequestDtoMapper::pickupRequestToPickupRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public PickUpRequestDto acceptRequest(long requestId, long ownerId) {
        PickupRequest request = findRequestAndCheckOwner(requestId, ownerId);

        if (request.getStatus() != PickupStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

        request.setStatus(PickupStatus.ACCEPTED);
        request.setAcceptedAt(LocalDateTime.now());
        // We expire the pickUpRequest after 24 hours
        request.setExpiresAt(LocalDateTime.now().plusHours(24));
        PickupRequest newPickupRequest = pickupRepository.save(request);

        return pickupRequestDtoMapper.pickupRequestToPickupRequestDto(newPickupRequest);
    }

    @Override
    @Transactional
    public PickUpRequestDto declineRequest(long requestId, long ownerId) {

        PickupRequest request = findRequestAndCheckOwner(requestId, ownerId);

        if (request.getStatus() != PickupStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

        request.setStatus(PickupStatus.REJECTED);
        PickupRequest newPickupRequest = pickupRepository.save(request);

        return pickupRequestDtoMapper.pickupRequestToPickupRequestDto(newPickupRequest);
    }

    @Override
    public PickUpRequestDto confirmRequest(long requestId, long userId) {
        PickupRequest request = pickupRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Pickup not found with id: " + requestId));

        boolean isOwner = request.getOwner().getId().equals(userId);
        boolean isRequester = request.getRequester().getId().equals(userId);

        if (!isOwner && !isRequester) {
            throw new AccessDeniedException("You are not allowed to request this pickup");
        }

        if (request.getStatus() != PickupStatus.ACCEPTED) {
            throw new IllegalStateException("Request is not accepted");
        }

        if (isOwner) {
            if (request.getOwnerConfirmedAt() != null) {
                throw new IllegalStateException("Pickup request has already been confirmed by owner");
            }
            request.setOwnerConfirmedAt(LocalDateTime.now());
        } else {
            if (request.getRequesterConfirmedAt() != null) {
                throw new IllegalStateException("Pickup request has already been confirmed by requester");
            }
            request.setRequesterConfirmedAt(LocalDateTime.now());
        }

        if (request.isFullyConfirmed()) {
            request.setCompletedAt(LocalDateTime.now());
            request.setStatus(PickupStatus.COMPLETED);
        }

        return pickupRequestDtoMapper.pickupRequestToPickupRequestDto(pickupRepository.save(request));
    }

    @Override
    @Transactional
    public PickUpRequestDto createPickupRequest(Long itemId, long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (item.getUser().getId().equals(userId)) {
            throw new IllegalStateException("The item belongs to the requester");
        }

        boolean alreadyExists = pickupRepository.existsByItem_IdAndRequester_IdAndStatusIn(itemId, userId, List.of(PickupStatus.PENDING, PickupStatus.ACCEPTED));

        if (alreadyExists) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "Du har allerede en aktiv anmodning på dette item"
            );
        }

        PickupRequest pickupRequest = new PickupRequest();
        pickupRequest.setRequester(user);
        pickupRequest.setItem(item);
        pickupRequest.setStatus(PickupStatus.PENDING);
        pickupRequest.setCreatedAt(LocalDateTime.now());
        pickupRequest.setOwner(item.getUser());
        pickupRepository.save(pickupRequest);

        return pickupRequestDtoMapper.pickupRequestToPickupRequestDto(pickupRequest);
    }

    public PickupRequest findRequestAndCheckOwner(long requestId, long ownerId) {
        PickupRequest request = pickupRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("You can not manage this request");
        }

        return request;
    }


}
