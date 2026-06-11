package com.recyclingprojectbackend.user.service;

import com.recyclingprojectbackend.user.dto.UserDto;
import com.recyclingprojectbackend.user.dto.UserDtoMapper;
import com.recyclingprojectbackend.user.dto.UserRequestDto;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        UserDtoMapper userDtoMapper = new UserDtoMapper();
        userService = new UserServiceImpl(userRepository, userDtoMapper);

        user1 = new User();
        user1.setName("Lars");
        user1.setId(1L);

        user2 = new User();
        user2.setName("Allan");
        user2.setId(2L);

    }

    @Test
    void findAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserDto> users = userService.findAllUsers();

        // Assert
        verify(userRepository).findAll();
        assertEquals(2, users.size(), "Expected a different number of users");
    }

    @Test
    void findLoggedInUser() {
        // Arrange
        UserDto expectedUser = new UserDto(
                1L,
                "Lasse",
                "Lasse@gmail.com",
                "xxx",
                "2100 København",
                "Jeg elsker genbrug"
        );

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                expectedUser,
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        UserDto result = userService.findLoggedInUser();

        // Assert
        assertEquals(expectedUser, result);
        assertEquals(1L, result.id());
        assertEquals("Lasse", result.name());
    }

    @Test
    void updateUserWhenUserNotFound() {
        // Arrange
        UserRequestDto request = new UserRequestDto(
                "Nyt navn",
                "København",
                "2100",
                "Ny bio"
        );

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class,
                () -> userService.updateUser(request, 999L));

        verify(userRepository, never()).save(any());
    }
}