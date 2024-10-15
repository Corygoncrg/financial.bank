package com.example.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterDto (@NotBlank String username,
                               @NotBlank @Email String email)
 {
}
