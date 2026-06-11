package com.recyclingprojectbackend.pickup_request.service;

import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.repository.ItemRepository;
import com.recyclingprojectbackend.item.util.ItemStatus;
import com.recyclingprojectbackend.notification.service.NotificationService;
import com.recyclingprojectbackend.notification.util.NotificationType;
import com.recyclingprojectbackend.pickup_request.dto.PickUpRequestDto;
import com.recyclingprojectbackend.pickup_request.dto.PickupRequestDtoMapper;
import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.repository.PickupRepository;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PickupServiceImpl implements PickupService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PickupRepository pickupRepository;
    private final PickupRequestDtoMapper pickupRequestDtoMapper;
    private final NotificationService notificationService;

    public PickupServiceImpl(ItemRepository itemRepository, UserRepository userRepository,  PickupRepository pickupRepository, PickupRequestDtoMapper pickupRequestDtoMapper,  NotificationService notificationService) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.pickupRepository = pickupRepository;
        this.pickupRequestDtoMapper = pickupRequestDtoMapper;
        this.notificationService = notificationService;
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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dette er ikke en igangværende anmodning");
        }

        request.setStatus(PickupStatus.ACCEPTED);
        request.setAcceptedAt(Instant.now());
        // We expire the pickUpRequest after 24 hours
        request.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        PickupRequest newPickupRequest = pickupRepository.save(request);

        // We update the item status
        Item item = request.getItem();
        item.setStatus(ItemStatus.RESERVED);
        item.setReservedAt(LocalDateTime.now());
        itemRepository.save(item);

        // We set all realted requests to DECLINED
        List<PickupRequest> otherRequests = pickupRepository.findByItem_IdAndStatusAndIdNot(
                item.getId(),
                PickupStatus.PENDING,
                requestId
        );

        for (PickupRequest otherRequest : otherRequests) {
            otherRequest.setStatus(PickupStatus.REJECTED);
            pickupRepository.save(otherRequest);

            notificationService.createNotification(
                    otherRequest.getRequester().getId(),
                    ownerId,
                    NotificationType.REQUEST_DECLINED,
                    "Din anmodning på " + item.getName() + " blev desværre ikke valgt",
                    otherRequest.getId()
            );
        }

        notificationService.createNotification(
                request.getRequester().getId(),
                request.getOwner().getId(),
                NotificationType.REQUEST_ACCEPTED,
                "Din anmodning blev accepteret!",
                request.getId());

        return pickupRequestDtoMapper.pickupRequestToPickupRequestDto(newPickupRequest);
    }

    @Override
    @Transactional
    public PickUpRequestDto declineRequest(long requestId, long ownerId) {

        PickupRequest request = findRequestAndCheckOwner(requestId, ownerId);

        if (request.getStatus() != PickupStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dette er ikke en igangværende anmodning");
        }

        request.setStatus(PickupStatus.REJECTED);
        PickupRequest newPickupRequest = pickupRepository.save(request);

        // We update the item status
        Item item = request.getItem();
        item.setStatus(ItemStatus.AVAILABLE);
        itemRepository.save(item);

        notificationService.createNotification(
                request.getRequester().getId(),
                request.getOwner().getId(),
                NotificationType.REQUEST_DECLINED,
                "Din anmodning blev ikke accepteret",
                request.getId());

        return pickupRequestDtoMapper.pickupRequestToPickupRequestDto(newPickupRequest);
    }

    @Transactional
    @Override
    public PickUpRequestDto confirmRequest(long requestId, long userId) {
        PickupRequest request = pickupRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Denne snatch blev ikek fundet"));

        boolean isOwner = request.getOwner().getId().equals(userId);
        boolean isRequester = request.getRequester().getId().equals(userId);

        if (!isOwner && !isRequester) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har ikke tilladelse til at anmode om denne snatch");
        }

        if (request.getStatus() != PickupStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Anmodningen er ikke accepteret");
        }

        if (isOwner) {
            if (request.getOwnerConfirmedAt() != null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Du har allerede bekræftet denne afhentning"
                );
            }
            request.setOwnerConfirmedAt(Instant.now());
            notificationService.createNotification(
                    request.getRequester().getId(),
                    request.getOwner().getId(),
                    NotificationType.PICKUP_COMPLETED,
                    request.getOwner().getName() + " har godkendt afhentning",
                    request.getId());

        } else {
            if (request.getRequesterConfirmedAt() != null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Du har allerede bekræftet denne afhentning"
                );
            }
            request.setRequesterConfirmedAt(Instant.now());
            notificationService.createNotification(
                    request.getOwner().getId(),
                    request.getRequester().getId(),
                    NotificationType.PICKUP_COMPLETED,
                    request.getRequester().getName() + " har godkendt afhentning",
                    request.getId());
        }

        Item item = request.getItem();

        if (request.isFullyConfirmed()) {
            request.setCompletedAt(Instant.now());
            request.setStatus(PickupStatus.COMPLETED);
            item.setStatus(ItemStatus.GIVEN_AWAY);
            itemRepository.save(item);

            // Request notification to the owner
            notificationService.createNotification(
                    request.getOwner().getId(),
                    request.getRequester().getId(),
                    NotificationType.PICKUP_COMPLETED,
                    "Din snatch er nu godkendt og afsluttet",
                    request.getId()
            );

            // Review notification to the owner
            notificationService.createNotification(
                    request.getOwner().getId(),
                    request.getRequester().getId(),
                    NotificationType.NEW_REVIEW,
                    "Giv " + request.getRequester().getName() + " en anmeldelse",
                    request.getId()
            );


            // Request notification the requester
            notificationService.createNotification(
                    request.getRequester().getId(),
                    request.getOwner().getId(),
                    NotificationType.PICKUP_COMPLETED,
                    "Tillykke med din nye snatch!",
                    request.getId()
            );

            // Review notification to the Requester
            notificationService.createNotification(
                    request.getRequester().getId(),
                    request.getOwner().getId(),
                    NotificationType.NEW_REVIEW,
                    "Giv " + request.getOwner().getName() + " en anmeldelse",
                    request.getId()
            );
        }

        return pickupRequestDtoMapper.pickupRequestToPickupRequestDto(pickupRepository.save(request));
    }

    @Override
    @Transactional
    public PickUpRequestDto createPickupRequest(Long itemId, long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Denne snatch blev ikke fundet"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Denne bruger blev ikke fundet"));

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Denne snatch er ikke længere tilgængelig");
        }

        if (item.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Du kan ikke anmode om din egen snatch"
            );
        }

        boolean alreadyExists = pickupRepository.existsByItem_IdAndRequester_IdAndStatusIn (itemId, userId, List.of(PickupStatus.PENDING, PickupStatus.ACCEPTED));

        if (alreadyExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Du har allerede en aktiv anmodning på denne snatch"
            );
        }

        PickupRequest pickupRequest = new PickupRequest();
        pickupRequest.setRequester(user);
        pickupRequest.setItem(item);
        pickupRequest.setStatus(PickupStatus.PENDING);
        pickupRequest.setCreatedAt(Instant.now());
        pickupRequest.setOwner(item.getUser());
        pickupRepository.save(pickupRequest);

        // create the notification
        notificationService.createNotification(
                pickupRequest.getOwner().getId(),
                pickupRequest.getRequester().getId(),
                NotificationType.PICKUP_REQUEST,
                "Du har fået en anmodning på dit item",
                pickupRequest.getId());

        return pickupRequestDtoMapper.pickupRequestToPickupRequestDto(pickupRequest);
    }

    public PickupRequest findRequestAndCheckOwner(long requestId, long ownerId) {
        PickupRequest request = pickupRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Denne anmodning blev ikke fundet"));

        if (!request.getOwner().getId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Du har ikke tilladelse til denne anmodning");
        }

        return request;
    }
}
