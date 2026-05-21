package com.recyclingprojectbackend.item_like.service;

import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.dto.ItemDtoMapper;
import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.repository.ItemRepository;
import com.recyclingprojectbackend.item_like.model.ItemLike;
import com.recyclingprojectbackend.item_like.repository.ItemLikeRepository;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemLikeServiceImpl implements ItemLikeService {

    private final ItemLikeRepository itemLikeRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemDtoMapper itemDtoMapper;

    public ItemLikeServiceImpl(ItemLikeRepository itemLikeRepository, ItemRepository itemRepository, UserRepository userRepository, ItemDtoMapper itemDtoMapper) {
        this.itemLikeRepository = itemLikeRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemDtoMapper = itemDtoMapper;
    }

    @Transactional
    @Override
    public void like(long itemId, long userId) {
        if (itemLikeRepository.existsByUser_IdAndItem_Id(userId, itemId)) {
            return;
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ItemLike itemLike = new ItemLike();
        itemLike.setItem(item);
        itemLike.setUser(user);
        itemLikeRepository.save(itemLike);
    }

    @Transactional
    @Override
    public void unLike(long itemId, long userId) {
        itemLikeRepository.findByUser_IdAndItem_Id(userId, itemId)
                .ifPresent(itemLikeRepository::delete);
    }

    @Transactional
    @Override
    public boolean hasLike(long itemId, long userId) {
        return itemLikeRepository.existsByUser_IdAndItem_Id(userId, itemId);
    }

    @Transactional
    @Override
    public long getLikeCount(long itemId) {
        return itemLikeRepository.countByItem_Id(itemId);
    }

    @Override
    public List<ItemDto> getLikedItems(long userId) {
        return itemLikeRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(like -> itemDtoMapper.itemToItemDtoForUser(like.getItem(), userId))
                .toList();
    }
}
