package com.example.financial.transactions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(
        @NotBlank String originalBank,
        @NotBlank String originalAgency,
        @NotBlank String originalAccount,
        @NotBlank String destinyBank,
        @NotBlank String destinyAgency,
        @NotBlank String destinyAccount,
        @NotNull BigDecimal amount,
        @NotNull  LocalDateTime transactionTime
) {
}
