package com.recyclingprojectbackend.item.service;

import com.recyclingprojectbackend.category.model.Category;
import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.dto.ItemRequestDto;
import com.recyclingprojectbackend.item.model.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface ItemService {
    List<ItemDto> getItems();
    ItemDto getItemById(long id);
    List<ItemDto> getItemsByCategory(String category);
    ItemDto addItem(@RequestBody ItemRequestDto itemRequestDto);

}
