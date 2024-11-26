package com.example.security.dto;


import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record UserAuthenticationDto(String username, String password, Collection<? extends GrantedAuthority> authorities, String status) {
}
