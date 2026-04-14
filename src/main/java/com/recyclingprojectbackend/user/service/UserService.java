package com.recyclingprojectbackend.user.service;

import com.recyclingprojectbackend.user.model.User;

import java.util.List;

public interface UserService {
    List<User> findAllUsers();
    User findUserById(long id);

}
