package com.recyclingprojectbackend.item.dto;

import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item_like.repository.ItemLikeRepository;
import org.springframework.stereotype.Component;

@Component
public class ItemDtoMapper {

    private final ItemLikeRepository itemLikeRepository;

    public ItemDtoMapper(ItemLikeRepository itemLikeRepository) {
        this.itemLikeRepository = itemLikeRepository;
    }

    public ItemDto itemToItemDto(Item item) {
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
                false
        );
    }

    public ItemDto itemToItemDtoForUser(Item item, long currentUserId) {
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
                itemLikeRepository.existsByUser_IdAndItem_Id(currentUserId, item.getId())
        );
    }
}
