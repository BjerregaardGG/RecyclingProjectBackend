package com.recyclingprojectbackend.user.dto;

import com.recyclingprojectbackend.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public UserDto userToUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
