package com.recyclingprojectbackend.item.service;

import com.recyclingprojectbackend.category.model.Category;
import com.recyclingprojectbackend.category.repository.CategoryRepository;
import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.dto.ItemDtoMapper;
import com.recyclingprojectbackend.item.dto.ItemRequestDto;
import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.repository.ItemRepository;
import com.recyclingprojectbackend.item.util.ItemStatus;
import com.recyclingprojectbackend.item_like.model.ItemLike;
import com.recyclingprojectbackend.item_like.repository.ItemLikeRepository;
import com.recyclingprojectbackend.pickup_request.repository.PickupRepository;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PickupRepository pickupRequestRepository;
    @Mock
    private ItemLikeRepository itemLikeRepository;

    private ItemServiceImpl itemService;

    private User user;

    @BeforeEach
    void setUp() {
        ItemDtoMapper itemDtoMapper = new ItemDtoMapper(itemLikeRepository, pickupRequestRepository);
        itemService = new ItemServiceImpl(
                itemRepository,
                itemDtoMapper,
                categoryRepository,
                userRepository,
                pickupRequestRepository
        );

        user = new User();
        user.setId(1L);
        user.setName("Emilie");

    }

    @Test
    void getAvailableItemsByUserId() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Til hjemmet");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setCategoryName("Elektronik");

        Item item = new Item();
        item.setId(1L);
        item.setName("Sofa");
        item.setStatus(ItemStatus.AVAILABLE);
        item.setUser(user);
        item.setCategory(category);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Computer");
        item2.setStatus(ItemStatus.AVAILABLE);
        item2.setUser(user);
        item2.setCategory(category2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByUser_IdNotAcceptedOrCompleted(user.getId(), ItemStatus.AVAILABLE))
                .thenReturn(List.of(item, item2));

        // Act
        List<ItemDto> availableItems = itemService.getAvailableItemsByUserId(user.getId(), ItemStatus.AVAILABLE);

        // Assert
        verify(itemRepository).findByUser_IdNotAcceptedOrCompleted(user.getId(), ItemStatus.AVAILABLE);
        assertEquals(2, availableItems.size(), "Expected 2 available items");
        assertEquals(item.getName(), availableItems.get(0).name(), "Expected different item name");
        assertEquals(item2.getName(), availableItems.get(1).name(), "Expected different item name");
    }

    @Test
    void addItem_validInput_savesItemWithCorrectFields() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Elektronik");

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "Tastatur",
                "God stand",
                "Fejler ingenting",
                category.getId(),
                "Tastatur.jpg",
                "Classensgade",
                "København",
                22222222.2222,
                33333333.33333
        );

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> {
            Item i = inv.getArgument(0);
            i.setId(50L);
            return i;
        });

        // Act
        ItemDto itemDto = itemService.addItem(itemRequestDto, user.getId());

        // Assert
        assertEquals("Tastatur", itemDto.name(), "Expected different item name");
        assertEquals("God stand", itemDto.description(), "Expected different item description");

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());

        Item savedItem = captor.getValue();
        assertEquals("Tastatur", savedItem.getName(), "Expected different item name");
        assertEquals("God stand", savedItem.getDescription(), "Expected different item description");
        assertEquals("Tastatur.jpg", savedItem.getImage(), "Expected different item image");
        assertEquals("Classensgade", savedItem.getAddress(), "Expected different item address");
        assertEquals("København", savedItem.getCity(), "Expected different item city");
        assertEquals(category.getId(), savedItem.getCategory().getId(), "Expected different item category id");
        assertEquals(user.getId(), savedItem.getUser().getId(), "Expected different item user id");
        assertEquals(ItemStatus.AVAILABLE, savedItem.getStatus(), "Expected different item status");
    }

    @Test
    void deleteAnotherUserItemById() {
        // Arrange
        Category category = new Category();
        category.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setUser(user);
        item.setCategory(category);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // Act + Assert
        assertThrows(ResponseStatusException.class,
                () -> itemService.deleteItemById(item.getId(), 100L));

        verify(itemRepository, never()).delete(any());
        verify(pickupRequestRepository, never()).delete(any());
    }
}