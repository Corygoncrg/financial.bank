package com.example.financial.transactions.model;

import com.example.financial.transactions.dto.TransactionDto;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity(name = "Transaction")
@Table(name = "transactions")
public class TransactionCsv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originBank;
    private String originAgency;
    private String originalAccount;
    private String destinyBank;
    private String destinyAgency;
    private String destinyAccount;
    private float amount;
    private LocalDateTime transactionTime;


    public TransactionCsv(TransactionDto dto) {
        this.originBank = dto.originBank();
        this.originAgency = dto.originAgency();
        this.originalAccount =dto.originalAccount();
        this.destinyBank = dto.destinyBank();
        this.destinyAgency = dto.destinyAgency();
        this.destinyAccount = dto.destinyAccount();
        this.amount = dto.amount();
        this.transactionTime = dto.transactionTime();
    }
}
