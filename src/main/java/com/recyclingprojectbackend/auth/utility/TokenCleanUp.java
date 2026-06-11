package com.recyclingprojectbackend.auth.utility;

import com.recyclingprojectbackend.auth.repository.PasswordResetTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

public class TokenCleanUp {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public TokenCleanUp(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}


