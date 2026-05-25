package com.recyclingprojectbackend.auth.service;

import com.recyclingprojectbackend.auth.dto.LoginRequestDto;
import com.recyclingprojectbackend.auth.dto.RegisterRequestDto;
import com.recyclingprojectbackend.auth.utility.AuthUtility;
import com.recyclingprojectbackend.auth.utility.JwtUtility;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtility jwtUtility;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthUtility authUtility;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void invalidPassword() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto("lasse@test.dk", "abc");

        when(authUtility.checkPassword("abc")).thenReturn(false);

        // Act + Assert
        assertThrows(RuntimeException.class,
                () -> authService.login(request));

        verify(authenticationManager, never()).authenticate(any());
        verify(userRepository, never()).findByEmail(any());
        verify(jwtUtility, never()).generateToken(any(Long.class));
    }

    @Test
    void wrongPassword() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto("lasse@test.dk", "WrongPassword123");

        when(authUtility.checkPassword("WrongPassword123")).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act + Assert
        assertThrows(BadCredentialsException.class,
                () -> authService.login(request));

        verify(userRepository, never()).findByEmail(any());
        verify(jwtUtility, never()).generateToken(any(Long.class));
    }

    @Test
    void register() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto(
                "lasse@test.dk",
                "Lasse",
                "Password123",
                "København",
                "2200"
        );

        when(authUtility.checkPassword(request.password())).thenReturn(true);
        when(authUtility.checkEmail(request.email())).thenReturn(true);
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("HashedPassword");

        // Act
        authService.register(request);

        // Assert
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("lasse@test.dk", savedUser.getEmail(), "Expected a different email address");
        assertEquals("Lasse", savedUser.getName(), "Expected a different name of user");
        assertEquals("HashedPassword", savedUser.getPassword(), "Expected a different password");
        assertEquals("København", savedUser.getCity(), "Expected a different city of user");
        assertEquals("2200", savedUser.getPostalCode(), "Expected a different postal code of user");
    }
}