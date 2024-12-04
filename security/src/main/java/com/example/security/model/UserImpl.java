package com.example.security.model;


import com.example.shared.dto.UserDto;
import com.example.shared.model.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class UserImpl implements UserDetails {

    private Long id;
    private String name;
    private String email;
    private String password;
    private UserStatus status;

    public UserImpl(UserDto dto) {
        this.name = dto.name();
        this.email = dto.email();
        this.status = UserStatus.valueOf(dto.status());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }
}
