package com.recyclingprojectbackend.item.dto;

import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item_like.repository.ItemLikeRepository;
import com.recyclingprojectbackend.pickup_request.repository.PickupRepository;
import com.recyclingprojectbackend.pickup_request.util.PickupStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemDtoMapper {

    private final ItemLikeRepository itemLikeRepository;
    private final PickupRepository pickupRepository;

    public ItemDtoMapper(ItemLikeRepository itemLikeRepository, PickupRepository pickupRepository) {
        this.itemLikeRepository = itemLikeRepository;
        this.pickupRepository = pickupRepository;
    }

    // This also accepts the logged in userId, so that we can decide the like status
    public ItemDto itemToItemDtoForUser(Item item, long currentUserId) {
        boolean hasActiveRequest = pickupRepository
                .existsByItem_IdAndRequester_IdAndStatusIn(
                        item.getId(),
                        currentUserId,
                        List.of(PickupStatus.PENDING, PickupStatus.ACCEPTED)
                );

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getSecondTitle(),
                item.getImage(),
                item.getCategory().getCategoryName(),
                item.getUser().getId(),
                item.getLatitude(),
                item.getLongitude(),
                item.getStatus(),
                item.getReservedAt(),
                itemLikeRepository.countByItem_Id(item.getId()),
                itemLikeRepository.existsByUser_IdAndItem_Id(currentUserId, item.getId()),
                hasActiveRequest
        );
    }
}
