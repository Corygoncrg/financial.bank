package com.example.shared.dto;

import com.example.shared.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public record UserDto(Long id, String name, String email, String status, List<String> authorities) {


    public UserDto(User user) {
        this(user.getId(), user.getName(), user.getEmail(), String.valueOf(user.getStatus()), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

}
