package com.example.users.model;

import com.example.shared.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserValidator {

    private Long id;
    private String uuid;
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
