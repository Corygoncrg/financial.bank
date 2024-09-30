package com.example.financial.transactions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TransactionDto(
        @NotBlank String originBank,
        @NotBlank String originAgency,
        @NotBlank String originalAccount,
        @NotBlank String destinyBank,
        @NotBlank String destinyAgency,
        @NotBlank String destinyAccount,
        @NotNull  float amount,
        @NotNull  LocalDateTime transactionTime
) {
}
