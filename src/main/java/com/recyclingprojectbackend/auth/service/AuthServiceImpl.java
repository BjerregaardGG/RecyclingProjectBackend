package com.recyclingprojectbackend.auth.service;

import com.recyclingprojectbackend.auth.dto.AuthResponseDto;
import com.recyclingprojectbackend.auth.dto.LoginRequestDto;
import com.recyclingprojectbackend.auth.dto.RegisterRequestDto;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return new AuthResponseDto("JWT", user.getEmail(), user.getId());
    }

    @Override
    public AuthResponseDto register(RegisterRequestDto request) {
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());

        if (userRepository.findByEmail(request.getEmail()) == null) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }
        User createdUser = userRepository.save(newUser);
        return new AuthResponseDto("JWT", createdUser.getEmail(), createdUser.getId());
    }
}
