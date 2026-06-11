package com.recyclingprojectbackend.item.service;

import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.dto.ItemRequestDto;
import com.recyclingprojectbackend.item.util.ItemStatus;

import java.util.List;


public interface ItemService {
    List<ItemDto> getItems(long userId);
    List<ItemDto> getLoggedInUserItems();
    ItemDto getItemById(long id, long userId);
    List<ItemDto> getAvailableItemsByUserId(long userId, ItemStatus status);
    ItemDto addItem(ItemRequestDto itemRequestDto, long userId);
    ItemDto deleteItemById(long id, long userId);

}
