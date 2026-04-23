package com.recyclingprojectbackend.item.service;

import com.recyclingprojectbackend.category.model.Category;
import com.recyclingprojectbackend.category.repository.CategoryRepository;
import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.dto.ItemDtoMapper;
import com.recyclingprojectbackend.item.dto.ItemRequestDto;
import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.repository.ItemRepository;
import com.recyclingprojectbackend.user.dto.UserDto;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemDtoMapper itemDtoMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, ItemDtoMapper itemDtoMapper, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.itemDtoMapper = itemDtoMapper;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ItemDto> getItems() {
        return itemRepository.findAll()
                .stream()
                .map(item -> itemDtoMapper.itemToItemDto(item))
                .toList();
    }

    @Override
    public ItemDto getItemById(long id) {
        Item item = itemRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        return itemDtoMapper.itemToItemDto(item);
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

        UserDto userDto = (UserDto) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item newItem = new Item();
        newItem.setCategory(category);
        newItem.setName(itemRequestDto.getName());
        newItem.setDescription(itemRequestDto.getDescription());
        newItem.setSecondTitle(itemRequestDto.getSecondTitle());
        newItem.setImage(itemRequestDto.getImage());
        newItem.setCity(itemRequestDto.getCity());
        newItem.setAddress(itemRequestDto.getAddress());
        newItem.setLatitude(itemRequestDto.getLatitude());
        newItem.setLongitude(itemRequestDto.getLongitude());
        newItem.setUser(user);

        return itemDtoMapper.itemToItemDto(itemRepository.save(newItem));
    }
}
