package com.example.financial.transactions.repository;

import com.example.financial.transactions.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TransactionRepository extends JpaRepository <Transaction, Long> {

    // Query to find existing transactions by their unique fields (e.g., date, account details, and amount)
    boolean existsByTransactionDate(@Param("transaction_date") LocalDateTime transactionDate);

    List<Transaction> findByTransactionDateIn(Set<LocalDateTime> transactionDates);

    List<Transaction> findByImportDate(@Param("import_date") LocalDateTime importDate);

    @Query("""
            SELECT t
            FROM Transaction t
            WHERE
            YEAR(t.transactionDate) = :year AND MONTH(t.transactionDate) = :month
            AND t.amount >= 100000
            """)
    List<Transaction> findSuspectTransactionsByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("""
        SELECT t.originalBank AS bank, t.originalAgency AS agency, t.originalAccount AS account,
               SUM(t.amount) AS totalAmountMoved, 'Exit' AS transferType
        FROM Transaction t
        WHERE YEAR(t.transactionDate) = :year AND MONTH(t.transactionDate) = :month
        GROUP BY t.originalBank, t.originalAgency, t.originalAccount
        HAVING SUM(t.amount) >= 1000000
        """)
    List<Object[]> findSuspectAccountsByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // Repository method for aggregated agency data
    @Query("""
        SELECT t.originalBank AS bank, t.originalAgency AS agency,
               SUM(t.amount) AS totalAmountMoved, 'Enter' AS transferType
        FROM Transaction t
        WHERE YEAR(t.transactionDate) = :year AND MONTH(t.transactionDate) = :month
        GROUP BY t.originalBank, t.originalAgency
        HAVING SUM(t.amount) >= 1000000000
        """)
    List<Object[]> findSuspectAgenciesByYearAndMonth(@Param("year") int year, @Param("month") int month);
}
