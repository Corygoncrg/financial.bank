package com.example.shared.dto;

import com.example.shared.model.UserValidator;

import java.time.Instant;
import java.util.UUID;

public record UserValidatorDto(Long id, UUID uuid, UserDto idUser, Instant expirationDate) {

    public UserValidatorDto(UserValidator validator) {
        this(validator.getId(), validator.getUuid(), new UserDto(validator.getIdUser()), validator.getExpirationDate());

    }
}
