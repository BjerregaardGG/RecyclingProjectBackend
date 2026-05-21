package com.recyclingprojectbackend.item_like.controller;


import com.recyclingprojectbackend.item.dto.ItemDto;
import com.recyclingprojectbackend.item_like.service.ItemLikeService;
import com.recyclingprojectbackend.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class ItemLikeController {

    private final ItemLikeService itemLikeService;

    public ItemLikeController(ItemLikeService itemLikeService) {
        this.itemLikeService = itemLikeService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<ItemDto>> getLikedItems(@AuthenticationPrincipal UserDto user) {
        return ResponseEntity.ok(itemLikeService.getLikedItems(user.id()));
    }

    @PostMapping("/like/{itemId}")
    public ResponseEntity<String> likeItem(@PathVariable long itemId, @AuthenticationPrincipal UserDto user ){
        itemLikeService.like(itemId, user.id());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unlike/{itemId}")
    public ResponseEntity<String> unlikeItem(@PathVariable long itemId, @AuthenticationPrincipal UserDto user ){
        itemLikeService.unLike(itemId, user.id());
        return ResponseEntity.ok().build();
    }
}
