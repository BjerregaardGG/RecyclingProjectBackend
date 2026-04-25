package com.recyclingprojectbackend.user.service;

import com.recyclingprojectbackend.user.dto.UserDto;
import com.recyclingprojectbackend.user.dto.UserDtoMapper;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    public UserDto findPublicUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return userDtoMapper.userToUserDto(user);
    }

    @Override
    public UserDto findUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return userDtoMapper.userToUserDto(user);
    }

    @Override
    public UserDto findLoggedInUser() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("Authentication object is null");
        }
        return (UserDto) authentication.getPrincipal();
    }

    @Override
    public String uploadPicture(String image) {

        UserDto userDto = (UserDto) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();

        if (userDto == null) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = userRepository.findById(userDto.id())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userDto.id()));

        user.setImage(image);
        userRepository.save(user);
        return image;
    }
}
