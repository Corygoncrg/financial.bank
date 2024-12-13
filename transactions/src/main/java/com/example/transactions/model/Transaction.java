package com.example.transactions.model;

import com.example.shared.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity(name = "Transaction")
@Table(name = "transactions")
public class Transaction {

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
    private LocalDateTime transactionDate;
    private LocalDateTime importDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User idUser;

    public Long getUserId() {
        return idUser != null ? idUser.getId() : null;
    }

}
