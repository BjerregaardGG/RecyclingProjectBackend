package com.recyclingprojectbackend.item.repository;

import com.recyclingprojectbackend.category.model.Category;
import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategory(Category category);
}
