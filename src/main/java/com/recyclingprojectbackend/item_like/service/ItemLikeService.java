package com.recyclingprojectbackend.item_like.service;

import com.recyclingprojectbackend.item.dto.ItemDto;

import java.util.List;

public interface ItemLikeService {
    void like (long itemId, long userId);
    void unLike(long itemId, long userId);
    List<ItemDto> getLikedItems(long userId);
}
