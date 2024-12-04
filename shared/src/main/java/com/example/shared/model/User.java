package com.example.shared.model;


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
//    public User(UserRegisterDto dto) {
//        this.name = dto.username();
//        this.email = dto.email();
//        this.password = String.format("%06d", new Random().nextInt(999999));
//        this.status = UserStatus.PENDING;
//    }

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
//    public void update(UserUpdateDto dto) {
//        if (dto.name() != null) {
//            this.name = dto.name();
//        }
//        if (dto.email() != null) {
//            this.email = dto.email();
//        }
//        if (dto.status() != null) {
//            this.status = dto.status();
//        }

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
