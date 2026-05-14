package com.recyclingprojectbackend.item.controller;

import com.recyclingprojectbackend.category.model.Category;
import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item.dto.ItemRequestDto;
import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.item.service.ItemService;
import com.recyclingprojectbackend.item.util.ItemStatus;
import com.recyclingprojectbackend.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/me")
    public ResponseEntity<List<ItemDto>> getLoggedInUserItems() {
        return ResponseEntity.ok(itemService.getLoggedInUserItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ItemDto>> getItemsByUserId(@PathVariable long userId) {
        return ResponseEntity.ok(itemService.getAvailableItemsByUserId(userId, ItemStatus.AVAILABLE));
    }

    @GetMapping("/category")
    public ResponseEntity<List<ItemDto>> getItemsByCategory(@RequestParam String category) {
        return ResponseEntity.ok(itemService.getItemsByCategory(category));
    }

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestBody ItemRequestDto itemRequestDto) {
        return ResponseEntity.ok(itemService.addItem(itemRequestDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ItemDto> deleteItemById(@PathVariable long id, @AuthenticationPrincipal UserDto user ) {
        return ResponseEntity.ok(itemService.deleteItemById(id, user.id()));
    }

}
