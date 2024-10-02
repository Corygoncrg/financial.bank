package com.example.financial.transactions.model;

import com.example.financial.transactions.dto.TransactionDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity(name = "Transaction")
@Table(name = "transactions")
public class TransactionCsv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalBank;
    private String originalAgency;
    private String originalAccount;
    private String destinyBank;
    private String destinyAgency;
    private String destinyAccount;
    private BigDecimal amount;
    private LocalDateTime transactionTime;

    public TransactionCsv(TransactionDto dto) {
        this.originalBank = dto.originalBank();
        this.originalAgency = dto.originalAgency();
        this.originalAccount =dto.originalAccount();
        this.destinyBank = dto.destinyBank();
        this.destinyAgency = dto.destinyAgency();
        this.destinyAccount = dto.destinyAccount();
        this.amount = dto.amount();
        this.transactionTime = dto.transactionTime();
    }
}
