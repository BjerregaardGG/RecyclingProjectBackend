package com.recyclingprojectbackend.user.service;

import com.recyclingprojectbackend.user.dto.UserDto;
import com.recyclingprojectbackend.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> findAllUsers();
    UserDto findUserById(long id);
    UserDto findLoggedInUser();
}
