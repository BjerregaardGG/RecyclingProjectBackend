package com.recyclingprojectbackend.item.dto;

import com.recyclingprojectbackend.item.model.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemDtoMapper {

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
                item.getLongitude()
        );
    }
}
