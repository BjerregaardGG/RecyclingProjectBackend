package com.recyclingprojectbackend.user.service;

import com.recyclingprojectbackend.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAllUsers();
    UserDto findPublicUserById(long id);
    UserDto findUserById(long id);
    UserDto findLoggedInUser();
    String uploadPicture(String image);
}
