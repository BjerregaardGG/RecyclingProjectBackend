package com.recyclingprojectbackend.auth.service;

import com.recyclingprojectbackend.auth.dto.AuthResponseDto;
import com.recyclingprojectbackend.auth.dto.LoginRequestDto;
import com.recyclingprojectbackend.auth.dto.RegisterRequestDto;

public interface AuthService {
    AuthResponseDto login(LoginRequestDto request);
    void register(RegisterRequestDto request);
}
