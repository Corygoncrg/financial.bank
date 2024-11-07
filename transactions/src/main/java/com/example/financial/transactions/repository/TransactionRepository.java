package com.example.financial.transactions.repository;

import com.example.financial.transactions.model.TransactionCsv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TransactionRepository extends JpaRepository <TransactionCsv, Long> {

    // Query to find existing transactions by their unique fields (e.g., date, account details, and amount)
    boolean existsByTransactionDate(@Param("transaction_date") LocalDateTime transactionDate);

    List<TransactionCsv> findByTransactionDateIn(Set<LocalDateTime> transactionDates);

    List<TransactionCsv> findByImportDate(@Param("import_date") LocalDateTime importDate);
}
