package com.recyclingprojectbackend.item.service;

import com.recyclingprojectbackend.category.model.Category;
import com.recyclingprojectbackend.category.repository.CategoryRepository;
import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.dto.ItemDtoMapper;
import com.recyclingprojectbackend.item.dto.ItemRequestDto;
import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.repository.ItemRepository;
import com.recyclingprojectbackend.pickup_request.model.PickupRequest;
import com.recyclingprojectbackend.pickup_request.repository.PickupRepository;
import com.recyclingprojectbackend.user.dto.UserDto;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemDtoMapper itemDtoMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PickupRepository pickupRequestRepository;

    public ItemServiceImpl(ItemRepository itemRepository, ItemDtoMapper itemDtoMapper, CategoryRepository categoryRepository, UserRepository userRepository, PickupRepository pickupRequestRepository) {
        this.itemRepository = itemRepository;
        this.itemDtoMapper = itemDtoMapper;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.pickupRequestRepository = pickupRequestRepository;
    }

    @Override
    public List<ItemDto> getItems() {
        return itemRepository.findAllAvailable()
                .stream()
                .map(item -> itemDtoMapper.itemToItemDto(item))
                .toList();
    }

    @Override
    public List<ItemDto> getLoggedInUserItems() {

        UserDto userDto = (UserDto) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();

        if (userDto == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return itemRepository.findByUser_Id(userDto.id())
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
        return itemRepository.findAvailableByCategory(category)
                .stream()
                .map(item -> itemDtoMapper.itemToItemDto(item))
                .toList();
    }

    @Override
    public ItemDto addItem(ItemRequestDto itemRequestDto) {
        Category category = categoryRepository.findById(itemRequestDto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        UserDto userDto = (UserDto) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findById(userDto.id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item newItem = new Item();
        newItem.setCategory(category);
        newItem.setName(itemRequestDto.name());
        newItem.setDescription(itemRequestDto.description());
        newItem.setSecondTitle(itemRequestDto.secondTitle());
        newItem.setImage(itemRequestDto.image());
        newItem.setCity(itemRequestDto.city());
        newItem.setAddress(itemRequestDto.address());
        newItem.setLatitude(itemRequestDto.latitude());
        newItem.setLongitude(itemRequestDto.longitude());
        newItem.setUser(user);

        return itemDtoMapper.itemToItemDto(itemRepository.save(newItem));
    }

    @Transactional
    @Override
    public ItemDto deleteItemById(long id, long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        if (item.getUser().getId() != userId) {
            throw new AccessDeniedException("You do not have the rights to delete this Item");
        }
        itemRepository.delete(item);

        PickupRequest request = pickupRequestRepository.findByItemId(item.getId());
        if (request == null) {
            throw new RuntimeException("Request not found");
        }
        pickupRequestRepository.delete(request);
        return itemDtoMapper.itemToItemDto(item);
    }
}
