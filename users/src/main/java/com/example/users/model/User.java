package com.example.users.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;

import java.security.Principal;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements SimpUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username")
    private String name;
    private String email;
    private String password;


    @Override
    public Principal getPrincipal() {
        return null;
    }

    @Override
    public boolean hasSessions() {
        return false;
    }

    @Override
    public SimpSession getSession(String sessionId) {
        return null;
    }

    @Override
    public Set<SimpSession> getSessions() {
        return Set.of();
    }
}
