package com.recyclingprojectbackend.auth.service;

import com.recyclingprojectbackend.auth.dto.AuthResponseDto;
import com.recyclingprojectbackend.auth.dto.LoginRequestDto;
import com.recyclingprojectbackend.auth.dto.RegisterRequestDto;
import com.recyclingprojectbackend.auth.utility.JwtUtility;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtility jwtUtility;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, JwtUtility jwtUtility, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtility = jwtUtility;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        String token = jwtUtility.generateToken(user.getId());
        return new AuthResponseDto(token, user.getEmail(), user.getId());
    }

    @Override
    public void register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("User already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(hashedPassword);

        userRepository.save(newUser);
    }

}
