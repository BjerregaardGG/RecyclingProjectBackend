package com.recyclingprojectbackend.pickup_request.components;

import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.repository.ItemRepository;
import com.recyclingprojectbackend.item.util.ItemStatus;
import com.recyclingprojectbackend.notification.service.NotificationService;
import com.recyclingprojectbackend.notification.util.NotificationType;
import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.repository.PickupRepository;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class PickupExpirationJob {

    private final PickupRepository pickupRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;

    public PickupExpirationJob(PickupRepository pickupRepository, ItemRepository itemRepository, NotificationService notificationService) {
        this.pickupRepository = pickupRepository;
        this.itemRepository = itemRepository;
        this.notificationService = notificationService;
    }

    // running every 5 minutes
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void expireOldPickups() {
        Instant cutOffTime = Instant.now();
        List<PickupRequest> expiredRequests = pickupRepository.findByStatusAndExpiresAtBefore(PickupStatus.ACCEPTED, cutOffTime);

        for (PickupRequest request : expiredRequests) {
            request.setStatus(PickupStatus.EXPIRED);
            pickupRepository.save(request);

            Item item = request.getItem();
            item.setStatus(ItemStatus.AVAILABLE);
            item.setReservedAt(null);
            itemRepository.save(item);

            notificationService.createNotification(
                    request.getOwner().getId(),
                    request.getRequester().getId(),
                    NotificationType.PICKUP_EXPIRED,
                    "Afhentning af " + item.getName() + " er udløbet",
                    request.getId()
            );

            notificationService.createNotification(
                    request.getRequester().getId(),
                    request.getOwner().getId(),
                    NotificationType.PICKUP_EXPIRED,
                    "Din anmodning på " + item.getName() + " er udløbet",
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

            // Review notification to the requester
            notificationService.createNotification(
                    request.getRequester().getId(),
                    request.getOwner().getId(),
                    NotificationType.NEW_REVIEW,
                    "Giv " + request.getOwner().getName() + " en anmeldelse",
                    request.getId()
            );
        }
    }
}
