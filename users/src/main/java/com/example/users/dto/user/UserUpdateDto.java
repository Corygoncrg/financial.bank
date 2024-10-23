package com.example.users.dto.user;

import com.example.users.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDto(@NotNull Long id,
                            String name, @Email String email, UserStatus status) {
}
