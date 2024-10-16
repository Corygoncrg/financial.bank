package com.example.users.model;

import com.example.users.dto.UserDto;

public class UserAdapter {

    public static UserDto userToDto(User user) {
        return new UserDto(user);
    }
}
