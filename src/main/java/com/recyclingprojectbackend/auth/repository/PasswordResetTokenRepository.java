package com.recyclingprojectbackend.auth.repository;

import com.recyclingprojectbackend.auth.model.PasswordResetToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    Optional<PasswordResetToken> findByToken(String token);
    @Modifying
    @Transactional
    void deleteByEmail(String mail);
    @Modifying
    @Transactional
    void deleteByExpiresAtBefore(LocalDateTime now);
}
