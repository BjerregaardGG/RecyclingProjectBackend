package com.recyclingprojectbackend.user.dto;

import com.recyclingprojectbackend.user.model.User;

public class UserPublicDtoMapper {

    public UserPublicDto userToUserPublicDto(User user) {

        return new UserPublicDto(user.getId(), user.getName(), user.getEmail());
    }

}
