package com.recyclingprojectbackend.auth.service;

import com.recyclingprojectbackend.auth.model.PasswordResetToken;
import com.recyclingprojectbackend.auth.repository.PasswordResetTokenRepository;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordServiceImplTest {

    @Mock
    private EmailService emailService;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ForgotPasswordServiceImpl forgotPasswordService;

    @Test
    void handleForgotPassword() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("lasse@test.dk");

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        // Act
        forgotPasswordService.handleForgotPasswordEmail("lasse@test.dk");

        // Assert
        verify(passwordResetTokenRepository).deleteByEmail("lasse@test.dk");
        verify(entityManager).flush();

        // Assert
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(captor.capture());

        PasswordResetToken savedToken = captor.getValue();
        assertEquals("lasse@test.dk", savedToken.getEmail());
        assertNotNull(savedToken.getToken());
        assertNotNull(savedToken.getExpiresAt());

        assertTrue(savedToken.getExpiresAt().isAfter(LocalDateTime.now().plusMinutes(19)));
        assertTrue(savedToken.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(21)));

        verify(emailService).sendPasswordResetEmail(
                eq(user.getEmail()),
                eq(savedToken.getToken())
        );
    }

    @Test
    void resetPasswordWithExpiredToken() {
        // Arrange
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken("token");
        passwordResetToken.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(passwordResetTokenRepository.findByToken("token")).thenReturn(java.util.Optional.of(passwordResetToken));

        assertThrows(RuntimeException.class,
                () -> forgotPasswordService.resetPassword(passwordResetToken.getToken(), "newPassword"));

        verify(userRepository, never()).findByEmail(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(passwordResetTokenRepository).delete(passwordResetToken);
    }
}