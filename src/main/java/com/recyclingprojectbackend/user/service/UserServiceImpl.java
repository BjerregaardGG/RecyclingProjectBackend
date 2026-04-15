package com.recyclingprojectbackend.user.service;

import com.recyclingprojectbackend.user.dto.UserDto;
import com.recyclingprojectbackend.user.dto.UserDtoMapper;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    public UserServiceImpl(UserRepository userRepository, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> userDtoMapper.userToUserDto(u))
                .toList();
    }

    @Override
    public UserDto findUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return userDtoMapper.userToUserDto(user);
    }
}
