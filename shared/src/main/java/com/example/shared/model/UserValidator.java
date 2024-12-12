package com.example.shared.model;

import com.example.shared.dto.UserValidatorDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneOffset;
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

    public UserValidator(UserValidatorDto dto) {
        this.id = dto.id();
        this.uuid = String.valueOf(dto.uuid());
        this.idUser = new User(dto.idUser());
        this.expirationDate = dto.expirationDate();
    }

    public void rebuild() {
        this.id = id+1;
        this.uuid = UUID.randomUUID().toString();
        this.expirationDate = Instant.now().plusMillis(900000);
    }

    public UUID getUuid() {
        return UUID.fromString(this.uuid);
    }

}

