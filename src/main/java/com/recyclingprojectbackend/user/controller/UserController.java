package com.recyclingprojectbackend.user.controller;

import com.recyclingprojectbackend.user.dto.UserDto;
import com.recyclingprojectbackend.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getLoggedInUser() {
        return ResponseEntity.ok(userService.findLoggedInUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findPublicUserById(id));
    }

    @PatchMapping("/me/image")
    public ResponseEntity<String> uploadPicture(@RequestParam String image) {
        return ResponseEntity.ok(userService.uploadPicture(image));
    }
}
