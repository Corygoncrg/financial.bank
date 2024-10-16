package com.example.users.dto;

import com.example.users.model.UserStatus;
import jakarta.validation.constraints.Email;

public record UserUpdateDto(Long id,
                            String name, @Email String email, UserStatus status) {
}
