package com.recyclingprojectbackend.auth.service;

import com.recyclingprojectbackend.auth.model.PasswordResetToken;
import com.recyclingprojectbackend.auth.repository.PasswordResetTokenRepository;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    public ForgotPasswordServiceImpl(EmailService emailService, PasswordResetTokenRepository passwordResetTokenRepository,
                                     UserRepository userRepository, PasswordEncoder passwordEncoder, EntityManager entityManager) {
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }


    @Override
    @Transactional
    public void handleForgotPasswordEmail(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Delete old token
        passwordResetTokenRepository.deleteByEmail(email);

        // Delete before insert
        entityManager.flush();

        // 2. Create new token
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetToken.setEmail(email);
        passwordResetToken.setExpiresAt(LocalDateTime.now().plusMinutes(20));
        passwordResetTokenRepository.save(passwordResetToken);

        // 3. Send the password reset mail with the new token
        emailService.sendPasswordResetEmail(email, passwordResetToken.getToken());
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        // 1. Check if password is expired --> delete
        if (passwordResetToken.isExpired()) {
            passwordResetTokenRepository.delete(passwordResetToken);
            throw new RuntimeException("Token has expired");
        }

        // 2. Find the user
        User user = userRepository.findByEmail(passwordResetToken.getEmail()).
                orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Set the new hashed password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 4. Delete token
        passwordResetTokenRepository.delete(passwordResetToken);
    }
}
