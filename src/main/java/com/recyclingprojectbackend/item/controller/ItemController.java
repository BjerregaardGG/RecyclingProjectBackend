package com.recyclingprojectbackend.item.controller;

import com.recyclingprojectbackend.category.model.Category;
import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.dto.ItemRequestDto;
import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems() {
        return ResponseEntity.ok(itemService.getItems());
    }

    @GetMapping("/category")
    public ResponseEntity<List<ItemDto>> getItemsByCategory(@RequestParam Category category) {
        return ResponseEntity.ok(itemService.getItemsByCategory(category));
    }

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestBody ItemRequestDto itemRequestDto) {
        return ResponseEntity.ok(itemService.addItem(itemRequestDto));
    }

}
