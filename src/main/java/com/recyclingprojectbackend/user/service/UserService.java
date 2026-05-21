package com.recyclingprojectbackend.user.service;

import com.recyclingprojectbackend.user.dto.UserDto;
import com.recyclingprojectbackend.user.dto.UserRequestDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAllUsers();
    UserDto findPublicUserById(long id);
    UserDto findUserById(long id);
    UserDto findLoggedInUser();
    String uploadPicture(String image);
    UserDto updateUser(UserRequestDto userDto, long userId);
}
