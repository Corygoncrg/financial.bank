package com.example.shared.model;


import com.example.shared.dto.UserDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Random;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username")
    private String name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserStatus status;


    public User(String name, String email, String password, UserStatus status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = status;
    }

    public User(UserDto dto) {
        this.id = dto.id();
        this.name = dto.name();
        this.email = dto.email();
        this.password = dto.password();
        this.status = UserStatus.valueOf(dto.status());
    }

    public void deactivate() {
        this.status = UserStatus.NOT_ACTIVE;
    }


    public void update(String name, String email, UserStatus status) {
        if (name != null) {
            this.name = name;
        }
        if (email != null) {
            this.email = email;
        }
        if (status != null) {
            this.status = status;
        }
    }

    public void validate() {
        this.status = UserStatus.ACTIVE;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return name;
    }

}
