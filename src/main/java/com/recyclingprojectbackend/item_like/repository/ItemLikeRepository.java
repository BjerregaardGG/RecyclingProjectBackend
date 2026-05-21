package com.recyclingprojectbackend.item_like.repository;

import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item_like.model.ItemLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemLikeRepository extends JpaRepository<ItemLike, Long> {
    boolean existsByUser_IdAndItem_Id(long userId, long itemId);
    Optional<ItemLike> findByUser_IdAndItem_Id(long userId, long itemId);
    long countByItem_Id(long itemId);
    List<ItemLike> findByUser_IdOrderByCreatedAtDesc(long userId);
}
