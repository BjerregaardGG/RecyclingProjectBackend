package com.recyclingprojectbackend.item.service;

import com.recyclingprojectbackend.category.model.Category;
import com.recyclingprojectbackend.category.repository.CategoryRepository;
import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.dto.ItemDtoMapper;
import com.recyclingprojectbackend.item.dto.ItemRequestDto;
import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.repository.ItemRepository;
import com.recyclingprojectbackend.item.util.ItemStatus;
import com.recyclingprojectbackend.pickup_request.repository.PickupRepository;
import com.recyclingprojectbackend.user.dto.UserDto;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kunne ikke finde bruger");
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
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kunne ikke finde snatch"));

        return itemDtoMapper.itemToItemDtoForUser(item, userId);
    }

    @Override
    public List<ItemDto> getAvailableItemsByUserId(long userId, ItemStatus status) {
        userRepository.findById(userId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kunne ikke finde bruger"));

        return itemRepository.findByUser_IdNotAcceptedOrCompleted(userId, status)
                .stream()
                .map(item -> itemDtoMapper.itemToItemDtoForUser(item, userId))
                .toList();
    }

    @Override
    public ItemDto addItem(ItemRequestDto itemRequestDto, long userId) {
        Category category = categoryRepository.findById(itemRequestDto.categoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Kategori ikke fundet"
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Bruger ikke fundet"
                ));

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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Kunne ikke finde denne snatch"));

        if (!item.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Du har ikke rettigheder til at slette denne snatch");
        }

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Du kan kun slette snatches der er tilgængelige");
        }

        ItemDto dto = itemDtoMapper.itemToItemDtoForUser(item, userId);

        pickupRequestRepository.deleteAllByItem_Id(item.getId());
        itemRepository.delete(item);

        return dto;
    }
}
