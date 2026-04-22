package com.recyclingprojectbackend.item.service;

import com.recyclingprojectbackend.category.model.Category;
import com.recyclingprojectbackend.category.repository.CategoryRepository;
import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.dto.ItemDtoMapper;
import com.recyclingprojectbackend.item.dto.ItemRequestDto;
import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemDtoMapper itemDtoMapper;
    private final CategoryRepository categoryRepository;

    public ItemServiceImpl(ItemRepository itemRepository, ItemDtoMapper itemDtoMapper,  CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.itemDtoMapper = itemDtoMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<ItemDto> getItems() {
        return itemRepository.findAll()
                .stream()
                .map(item -> itemDtoMapper.itemToItemDto(item))
                .toList();
    }

    @Override
    public List<ItemDto> getItemsByCategory(String category) {
        return itemRepository.findByCategory_CategoryName(category)
                .stream()
                .map(item -> itemDtoMapper.itemToItemDto(item))
                .toList();
    }

    @Override
    public ItemDto addItem(ItemRequestDto itemRequestDto) {
        Category category = categoryRepository.findById(itemRequestDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Item newItem = new Item();
        newItem.setCategory(category);
        newItem.setName(itemRequestDto.getName());
        newItem.setDescription(itemRequestDto.getDescription());
        newItem.setSecondTitle(itemRequestDto.getSecondTitle());
        newItem.setImage(itemRequestDto.getImage());

        return itemDtoMapper.itemToItemDto(itemRepository.save(newItem));
    }
}
