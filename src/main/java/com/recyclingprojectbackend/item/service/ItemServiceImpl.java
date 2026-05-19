package com.recyclingprojectbackend.item.service;

import com.recyclingprojectbackend.category.model.Category;
import com.recyclingprojectbackend.category.repository.CategoryRepository;
import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.dto.ItemDtoMapper;
import com.recyclingprojectbackend.item.dto.ItemRequestDto;
import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.repository.ItemRepository;
import com.recyclingprojectbackend.item.util.ItemStatus;
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
    public List<ItemDto> getItems(long userId) {
        return itemRepository.findAllAvailable()
                .stream()
                .map(item -> itemDtoMapper.itemToItemDtoForUser(item, userId ))
                .toList();
    }

    @Override
    public List<ItemDto> getLoggedInUserItems() {

        UserDto userDto = (UserDto) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();

        if (userDto == null) {
            throw new UsernameNotFoundException("User not found");
        }

        long userId = userDto.id();

        return itemRepository.findByUser_Id(userId)
                .stream()
                .map(item -> itemDtoMapper.itemToItemDtoForUser(item, userId))
                .toList();
    }

    @Override
    public ItemDto getItemById(long id, long userId) {
        Item item = itemRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        return itemDtoMapper.itemToItemDtoForUser(item, userId);
    }

    @Override
    public List<ItemDto> getAvailableItemsByUserId(long userId, ItemStatus status) {
        userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return itemRepository.findByUser_IdNotAcceptedOrCompleted(userId, status)
                .stream()
                .map(item -> itemDtoMapper.itemToItemDtoForUser(item, userId))
                .toList();
    }

    @Override
    public ItemDto addItem(ItemRequestDto itemRequestDto, long userId) {
        Category category = categoryRepository.findById(itemRequestDto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        User user = userRepository.findById(userId)
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

        return itemDtoMapper.itemToItemDtoForUser(itemRepository.save(newItem), userId);
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
        return itemDtoMapper.itemToItemDtoForUser(item, userId);
    }
}
