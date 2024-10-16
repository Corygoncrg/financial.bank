package com.example.users.dto;

import com.example.users.model.User;

import java.util.List;

public record UserDto(Long id, String name, String email) {

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
    //fixme finish this construction
}
