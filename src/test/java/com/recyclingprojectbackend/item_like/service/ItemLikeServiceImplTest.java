package com.recyclingprojectbackend.item_like.service;

import com.recyclingprojectbackend.item.dto.ItemDtoMapper;
import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.repository.ItemRepository;
import com.recyclingprojectbackend.item.util.ItemStatus;
import com.recyclingprojectbackend.item_like.model.ItemLike;
import com.recyclingprojectbackend.item_like.repository.ItemLikeRepository;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemLikeServiceImplTest {

    @Mock
    private ItemLikeRepository itemLikeRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private ItemLikeServiceImpl itemLikeService;
    private Item item;
    private ItemLike itemLike;
    private User user;

    @BeforeEach
    void setUp() {
        ItemDtoMapper itemDtoMapper = new ItemDtoMapper(itemLikeRepository);
        itemLikeService = new ItemLikeServiceImpl(
                itemLikeRepository,
                itemRepository,
                userRepository,
                itemDtoMapper
        );

        item = new Item();
        item.setId(1L);
        item.setStatus(ItemStatus.AVAILABLE);
        item.setName("Sofa");
        item.setImage("Sofa.jpg");

        itemLike = new ItemLike();
        itemLike.setItem(item);

        user = new User();
        user.setId(2L);
        user.setName("Jan");
    }

    @Test
    void likeNewItem() {
        // Arrange
        when(itemLikeRepository.existsByUser_IdAndItem_Id(user.getId(), item.getId())).thenReturn(false);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        itemLikeService.like(item.getId(), user.getId());

        // Assert
        verify(itemLikeRepository).save(any(ItemLike.class));
    }

    @Test
    void likeAlreadyLikedItem() {
        // Arrange
        when(itemLikeRepository.existsByUser_IdAndItem_Id(user.getId(), item.getId())).thenReturn(true);

        // Act
        itemLikeService.like(item.getId(), user.getId());

        // Assert
        verify(itemLikeRepository, never()).save(any(ItemLike.class));
        verify(itemRepository, never()).findById(any());
        verify(userRepository, never()).findById(any());
    }
}