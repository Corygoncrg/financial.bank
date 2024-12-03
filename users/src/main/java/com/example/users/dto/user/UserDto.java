package com.example.users.dto.user;

import com.example.users.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public record UserDto(Long id, String name, String email, String status, List<String> authorities) {


    public UserDto(User user) {
        this(user.getId(), user.getName(), user.getEmail(), String.valueOf(user.getStatus()), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

}
