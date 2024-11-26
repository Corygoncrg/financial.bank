package com.example.security.dto;


import com.example.security.model.User;

public record UserDto(Long id, String name, String email, String status) {


    public UserDto(User d) {
        this(d.getId(), d.getName(), d.getEmail(), String.valueOf(d.getStatus()));
    }

}
