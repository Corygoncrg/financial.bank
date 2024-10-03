package com.example.financial.transactions;

import com.example.financial.transactions.model.TransactionCsv;
import com.example.financial.transactions.model.TransactionCsvRecord;
import com.example.financial.transactions.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.financial.transactions.Service.TransactionRecordToCsvService.transactionAdapter;

@Service
public class TransactionCsvService {

    @Autowired
    private TransactionRepository repository;

    private LocalDateTime firstTransactionDate = null;

    public TransactionCsv validateDate(TransactionCsvRecord csvRecord) {
        LocalDateTime currentTransactionTime = csvRecord.getTransactionTime();

        if (firstTransactionDate == null) {
            firstTransactionDate = currentTransactionTime;
            System.out.println("First transaction date: " + firstTransactionDate);
        }

        if (isSameDay(currentTransactionTime, firstTransactionDate)) {
            if (repository.existsByTransactionTime(currentTransactionTime)) {
                // If the transaction is a duplicate, log and skip it
                System.out.println("Duplicate transaction detected, skipping: " + currentTransactionTime);
                return null;  // Skip the record if it's a duplicate
            }
            System.out.println("Processing transaction with date: " + currentTransactionTime);
            TransactionCsv transaction = transactionAdapter(csvRecord);
            return transaction;
        } else {
            System.out.println("Skipping transaction with date: " + currentTransactionTime);
        }
        return null;
    }
    
    private boolean isSameDay(LocalDateTime date1, LocalDateTime date2) {
        return date1.getDayOfMonth() == date2.getDayOfMonth()
                && date1.getMonth() == date2.getMonth()
                && date1.getYear() == date2.getYear();
    }
}

