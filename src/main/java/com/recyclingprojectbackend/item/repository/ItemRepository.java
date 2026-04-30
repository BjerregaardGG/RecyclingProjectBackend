package com.recyclingprojectbackend.item.repository;

import com.recyclingprojectbackend.category.model.Category;
import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    // Find items that does not have status = ACCEPTED
    @Query("""
        SELECT i FROM Item i
        WHERE i.id NOT IN (
            SELECT p.item.id FROM PickupRequest p
            WHERE p.status = 'ACCEPTED'
        )
    """)
    List<Item> findAllAvailable();

    // Find items that does not have status = ACCEPTED by Category
    @Query("""
        SELECT i FROM Item i
        WHERE i.category.categoryName = :categoryName
        AND i.id NOT IN (
            SELECT p.item.id FROM PickupRequest p
            WHERE p.status = 'ACCEPTED'
        )
    """)
    List<Item> findAvailableByCategory(@Param("categoryName") String categoryName);
    List<Item> findByCategory_CategoryName(String categoryName);
    List<Item> findByUser_Id(long id);
}
