package com.example.financial.transactions.repository;

import com.example.financial.transactions.model.TransactionCsv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository <TransactionCsv, Long> {

    // Query to find existing transactions by their unique fields (e.g., date, account details, and amount)
    boolean existsByTransactionTime(@Param("transaction_time") LocalDateTime transactionTime);
}
