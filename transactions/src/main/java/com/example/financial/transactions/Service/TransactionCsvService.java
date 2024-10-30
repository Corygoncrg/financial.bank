package com.example.financial.transactions.Service;

import com.example.financial.transactions.model.TransactionCsv;
import com.example.financial.transactions.model.TransactionCsvRecord;
import com.example.financial.transactions.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.financial.transactions.model.TransactionAdapter.transactionRecordToCsvAdapter;

@Service
public class TransactionCsvService {

    @Autowired
    private TransactionRepository repository;

    private LocalDateTime firstTransactionDate = null;

    public TransactionCsv processRecord(TransactionCsvRecord csvRecord, LocalDateTime importDate) {
        LocalDateTime currentTransactionTime = csvRecord.getTransactionDate();

        if (firstTransactionDate == null) {
            firstTransactionDate = currentTransactionTime;
            System.out.println("First transaction date: " + firstTransactionDate);
        }

        if (isSameDay(currentTransactionTime, firstTransactionDate)) {
            if (repository.existsByTransactionDate(currentTransactionTime)) {
                // If the transaction is a duplicate, log and skip it
                System.out.println("Duplicate transaction detected, skipping: " + currentTransactionTime);
                return null;  // Skip the record if it's a duplicate
            }
            System.out.println("Processing transaction with date: " + currentTransactionTime);

//TODO: using the token that is necessary to access the page
// extract the user ID and insert into the csvRecord so that it can be saved in the database

            return transactionRecordToCsvAdapter(csvRecord, importDate);
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

