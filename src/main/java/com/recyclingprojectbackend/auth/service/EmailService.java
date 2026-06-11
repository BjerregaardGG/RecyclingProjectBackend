package com.recyclingprojectbackend.auth.service;

public interface EmailService {
    void sendPasswordResetEmail(String email, String token);
}
