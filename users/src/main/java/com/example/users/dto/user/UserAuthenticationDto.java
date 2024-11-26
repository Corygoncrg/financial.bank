package com.example.users.dto.user;


import com.example.users.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record UserAuthenticationDto(String username, String password, Collection<? extends GrantedAuthority> authorities, String status) {

    public UserAuthenticationDto(User user) {
        this(user.getUsername(), user.getPassword(), user.getAuthorities(), String.valueOf(user.getStatus()));
    }
}
