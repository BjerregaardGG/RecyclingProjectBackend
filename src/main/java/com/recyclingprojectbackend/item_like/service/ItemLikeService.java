package com.recyclingprojectbackend.item_like.service;

public interface ItemLikeService {
    void like (long itemId, long userId);
    void unLike(long itemId, long userId);
    boolean hasLike(long itemId, long userId);
    long getLikeCount(long itemId);
}
