package com.example.shared.dto;


import com.example.shared.model.User;

import java.util.Collection;

public record UserAuthenticationDto(String username, String password, Collection<?> authorities, String status) {

    public UserAuthenticationDto(User user) {
        this(user.getUsername(), user.getPassword(), user.getAuthorities(), String.valueOf(user.getStatus()));
    }
}
