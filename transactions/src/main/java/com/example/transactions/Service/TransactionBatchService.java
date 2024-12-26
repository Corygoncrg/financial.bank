package com.example.transactions.Service;

import com.example.shared.dto.UserDto;
import com.example.shared.model.User;
import com.example.transactions.model.Transaction;
import com.example.transactions.model.TransactionRecord;
import com.example.transactions.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.transactions.model.TransactionAdapter.transactionRecordToTransactionAdapter;

@Service
public class TransactionBatchService {

    @Autowired
    private TransactionRepository repository;

    private LocalDateTime firstTransactionDate = null;

    public Transaction processRecord(TransactionRecord record, LocalDateTime importDate, UserDto dto) {
        LocalDateTime currentTransactionTime = record.getTransactionDate();

        if (firstTransactionDate == null) {
            firstTransactionDate = currentTransactionTime;
            System.out.println("First transaction date: " + firstTransactionDate);
        }

        if (isSameDay(currentTransactionTime, firstTransactionDate)) {
            if (repository.existsByTransactionDate(currentTransactionTime)) {
                System.out.println("Duplicate transaction detected, skipping: " + currentTransactionTime);
                return null;
            }
            System.out.println("Processing transaction with date: " + currentTransactionTime);
            
            record.setIdUser(new User(dto));
            return transactionRecordToTransactionAdapter(record, importDate);
        } else {
            System.out.println("Skipping transaction with date: " + currentTransactionTime);
        }
        return null;
    }

    public void resetState() {
        firstTransactionDate = null;
        System.out.println("Transaction state reset.");
    }


    private boolean isSameDay(LocalDateTime date1, LocalDateTime date2) {
        return date1.getDayOfMonth() == date2.getDayOfMonth()
                && date1.getMonth() == date2.getMonth()
                && date1.getYear() == date2.getYear();
    }

}

