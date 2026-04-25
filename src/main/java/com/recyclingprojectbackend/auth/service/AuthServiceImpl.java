package com.recyclingprojectbackend.auth.service;

import com.recyclingprojectbackend.auth.dto.AuthResponseDto;
import com.recyclingprojectbackend.auth.dto.LoginRequestDto;
import com.recyclingprojectbackend.auth.dto.RegisterRequestDto;
import com.recyclingprojectbackend.auth.utility.JwtUtility;
import com.recyclingprojectbackend.auth.utility.AuthUtility;
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
    private final AuthUtility authUtility;

    public AuthServiceImpl(UserRepository userRepository, JwtUtility jwtUtility, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, AuthUtility authUtility) {
        this.userRepository = userRepository;
        this.jwtUtility = jwtUtility;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.authUtility = authUtility;
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        if (!authUtility.checkPassword(request.password())) {
            throw new RuntimeException("Password does not meet the requirements");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email()).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        String token = jwtUtility.generateToken(user.getId());
        return new AuthResponseDto(token, user.getEmail(), user.getId());
    }

    @Override
    public void register(RegisterRequestDto request) {
        if (!authUtility.checkPassword(request.password())) {
            throw new RuntimeException("Password does not meet the requirements");
        }
        if (!authUtility.checkEmail(request.email())) {
            throw new RuntimeException("Email does not meet the requirements");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalStateException("User already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        User newUser = new User();
        newUser.setEmail(request.email());
        newUser.setName(request.name());
        newUser.setPassword(hashedPassword);
        newUser.setCity(request.city());
        newUser.setPostalCode(request.postalCode());

        userRepository.save(newUser);
    }

}
