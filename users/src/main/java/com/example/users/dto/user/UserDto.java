package com.example.users.dto.user;

import com.example.users.model.User;

public record UserDto(Long id, String name, String email, String status) {


    public UserDto(User user) {
        this(user.getId(), user.getName(), user.getEmail(), String.valueOf(user.getStatus()));
    }

}
