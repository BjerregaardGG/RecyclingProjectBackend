package com.recyclingprojectbackend.user.service;

import com.recyclingprojectbackend.user.dto.UserDto;
import com.recyclingprojectbackend.user.dto.UserDtoMapper;
import com.recyclingprojectbackend.user.dto.UserRequestDto;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                .map(userDtoMapper::userToUserDto)
                .toList();
    }

    @Override
    public UserDto findUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bruger ikke fundet"));

        return userDtoMapper.userToUserDto(user);
    }

    @Override
    public UserDto findLoggedInUser() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Du skal være logget ind"
            );
        }
        return (UserDto) authentication.getPrincipal();
    }

    @Override
    public String uploadPicture(String image) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Du skal være logget ind"
            );
        }

        UserDto userDto = (UserDto) authentication.getPrincipal();

        User user = userRepository.findById(userDto.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bruger ikke fundet"));

        user.setImage(image);
        userRepository.save(user);
        return image;
    }

    @Override
    public UserDto updateUser(UserRequestDto userRequest, long userId) {

        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bruger ikke fundet"));

        if (userRequest.name() != null && !userRequest.name().isBlank()){
            user.setName(userRequest.name().trim());
        }

        if (userRequest.city() != null && !userRequest.city().isBlank()) {
            user.setCity(userRequest.city().trim().isEmpty() ? null : userRequest.city().trim());
        }

        if (userRequest.postalCode() != null && !userRequest.postalCode().isBlank()) {
            user.setPostalCode(userRequest.postalCode().trim().isEmpty() ? null : userRequest.postalCode().trim());
        }

        if (userRequest.profileText() != null) {
            user.setProfileText(userRequest.profileText().trim().isEmpty() ? null : userRequest.profileText().trim());
        }

        User savedUser = userRepository.save(user);
        return userDtoMapper.userToUserDto(savedUser);
    }
}
