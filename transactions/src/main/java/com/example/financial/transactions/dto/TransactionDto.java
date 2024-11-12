package com.example.financial.transactions.dto;

import com.example.financial.transactions.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(
        String originalBank,
        String originalAgency,
        String originalAccount,
        String destinyBank,
        String destinyAgency,
        String destinyAccount,
        BigDecimal amount,
        LocalDateTime transactionDate,
        LocalDateTime importDate,
        String idUser) {

    public static TransactionDto from(Transaction transaction) {
        return new TransactionDto(
                transaction.getOriginalBank(),
                transaction.getOriginalAgency(),
                transaction.getOriginalAccount(),
                transaction.getDestinyBank(),
                transaction.getDestinyAgency(),
                transaction.getDestinyAccount(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getImportDate(),
                transaction.getIdUser().getName()
        );
    }
}
