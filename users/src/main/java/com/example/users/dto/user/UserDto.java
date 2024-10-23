package com.example.users.dto.user;

import com.example.users.model.User;

public record UserDto(Long id, String name, String email, String status) {


    public UserDto(User d) {
        this(d.getId(), d.getName(), d.getEmail(), String.valueOf(d.getStatus()));
    }

}
