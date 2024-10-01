package com.example.financial.transactions.repository;

import com.example.financial.transactions.model.TransactionCsv;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository <TransactionCsv, Long> {
}
