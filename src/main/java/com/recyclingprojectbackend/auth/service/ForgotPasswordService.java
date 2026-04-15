package com.recyclingprojectbackend.auth.service;

public interface ForgotPasswordService {
    void handleForgotPasswordEmail(String email);
    void resetPassword(String newPassword, String token);
}
