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
import com.recyclingprojectbackend.review.model.Review;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PickupServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PickupRepository pickupRepository;
    @Mock
    private NotificationService notificationService;

    private PickupServiceImpl pickupService;

    private User owner;
    private User requester;
    private Item item;
    private PickupRequest pickupRequest;

    @BeforeEach
    void setUp() {
        PickupRequestDtoMapper pickupRequestDtoMapper = new PickupRequestDtoMapper();

        pickupService = new PickupServiceImpl(
                itemRepository,
                userRepository,
                pickupRepository,
                pickupRequestDtoMapper,
                notificationService
        );

        owner = new User();
        owner.setId(1L);
        owner.setEmail("lasse@email.com");
        owner.setName("Lasse");

        requester = new User();
        requester.setId(2L);
        requester.setEmail("emilie@email.com");
        requester.setName("Emilie");

        item = new Item();
        item.setId(1L);
        item.setName("Sofa");
        item.setStatus(ItemStatus.AVAILABLE);

        pickupRequest = new PickupRequest();
        pickupRequest.setId(100L);
        pickupRequest.setOwner(owner);
        pickupRequest.setRequester(requester);
        pickupRequest.setItem(item);
        pickupRequest.setStatus(PickupStatus.PENDING);

    }

    @Test
    void acceptRequest() {
        // Arrange
        when(pickupRepository.findById(100L)).thenReturn(Optional.of(pickupRequest));
        when(pickupRepository.save(any(PickupRequest.class))).thenReturn(pickupRequest);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // Act
        PickUpRequestDto request = pickupService.acceptRequest(pickupRequest.getId(), owner.getId());

        // Assert
        verify(pickupRepository).save(any(PickupRequest.class));
        verify(itemRepository).save(any(Item.class));
        verify(pickupRepository).findByItem_IdAndStatusAndIdNot(item.getId(), PickupStatus.PENDING, pickupRequest.getId());
        verify(notificationService).createNotification(
                eq(requester.getId()),
                eq(owner.getId()),
                eq(NotificationType.REQUEST_ACCEPTED),
                any(String.class),
                eq(pickupRequest.getId())
        );
        assertEquals(request.ownerName(), owner.getName(), "Expected a different owner name");
        assertEquals(request.requesterName(), requester.getName(), "Expected a different requester name");
        assertEquals(request.itemId(), item.getId(), "Expected a different item id");
        assertEquals(request.id(), pickupRequest.getId(), "Expected a different request id");
    }

    @Test
    void declineRequestThatIsNotPending() {
        // Arrange
        pickupRequest.setStatus(PickupStatus.ACCEPTED);

        when(pickupRepository.findById(100L)).thenReturn(Optional.of(pickupRequest));

        // Act + Assert
        assertThrows(IllegalStateException.class,
                () -> pickupService.declineRequest(100L, owner.getId()));

        verify(pickupRepository, never()).save(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void ownerConfirmsRequest() {
        // Arrange
        pickupRequest.setStatus(PickupStatus.ACCEPTED);
        pickupRequest.setRequesterConfirmedAt(Instant.now().minusSeconds(60));

        when(pickupRepository.findById(100L)).thenReturn(Optional.of(pickupRequest));
        when(pickupRepository.save(any(PickupRequest.class))).thenAnswer(inv -> inv.getArgument(0));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act - Owner confirms
        pickupService.confirmRequest(pickupRequest.getId(), owner.getId());

        // Assert
        assertNotNull(pickupRequest.getOwnerConfirmedAt());
        assertNotNull(pickupRequest.getRequesterConfirmedAt());
        assertEquals(PickupStatus.COMPLETED, pickupRequest.getStatus());
        assertEquals(ItemStatus.GIVEN_AWAY, item.getStatus());
        verify(itemRepository).save(any(Item.class));
    }
}