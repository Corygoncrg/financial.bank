package com.example.financial.transactions.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionCsvRecord {
    private String originalBank;
    private String originalAgency;
    private String originalAccount;
    private String destinyBank;
    private String destinyAgency;
    private String destinyAccount;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private LocalDateTime importDate;
    private User idUser;
}
