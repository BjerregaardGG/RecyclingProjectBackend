package com.recyclingprojectbackend.auth.controller;

import com.recyclingprojectbackend.auth.dto.AuthResponseDto;
import com.recyclingprojectbackend.auth.dto.LoginRequestDto;
import com.recyclingprojectbackend.auth.dto.RegisterRequestDto;
import com.recyclingprojectbackend.auth.service.AuthService;
import com.recyclingprojectbackend.auth.service.AuthServiceImpl;
import com.recyclingprojectbackend.auth.service.ForgotPasswordService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final ForgotPasswordService forgotPasswordService;

    public AuthController(AuthServiceImpl authService, ForgotPasswordService forgotPasswordService) {
        this.authService = authService;
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        forgotPasswordService.handleForgotPasswordEmail(email);
        return ResponseEntity.ok("Email has been sent successfully");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String password) {
        forgotPasswordService.resetPassword(token, password);
        return ResponseEntity.ok("Password has reset successfully");
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
