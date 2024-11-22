package com.example.users.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users_validator")
public class UserValidator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", length = 36)
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User idUser;

    private Instant expirationDate;

    public UserValidator(User user) {
        this.idUser = user;
        this.uuid = UUID.randomUUID().toString();
        this.expirationDate = Instant.now().plusMillis(900000);
    }

    public UUID getUuid() {
        return UUID.fromString(this.uuid);
    }

}
