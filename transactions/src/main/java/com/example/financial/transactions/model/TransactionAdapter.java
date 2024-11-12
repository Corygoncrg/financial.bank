package com.example.financial.transactions.model;

import java.time.LocalDateTime;

public class TransactionAdapter {

    public static Transaction transactionRecordToTransactionAdapter(TransactionRecord record, LocalDateTime importDate) {
        Transaction transaction = new Transaction();
        transaction.setOriginalBank(record.getOriginalBank());
        transaction.setOriginalAgency(record.getOriginalAgency());
        transaction.setOriginalAccount(record.getOriginalAccount());
        transaction.setDestinyBank(record.getDestinyBank());
        transaction.setDestinyAgency(record.getDestinyAgency());
        transaction.setDestinyAccount(record.getDestinyAccount());
        transaction.setAmount(record.getAmount());
        transaction.setTransactionDate(record.getTransactionDate());
        transaction.setImportDate(importDate);
        transaction.setIdUser(record.getIdUser());
        return transaction;
    }

    public static TransactionRecord transactionToRecordAdapter(Transaction transaction) {
        TransactionRecord record = new TransactionRecord();
        record.setOriginalBank(transaction.getOriginalBank());
        record.setOriginalAgency(transaction.getOriginalAgency());
        record.setOriginalAccount(transaction.getOriginalAccount());
        record.setDestinyBank(transaction.getDestinyBank());
        record.setDestinyAgency(transaction.getDestinyAgency());
        record.setDestinyAccount(transaction.getDestinyAccount());
        record.setAmount(transaction.getAmount());
        record.setTransactionDate(transaction.getTransactionDate());
        record.setImportDate(transaction.getImportDate());
        record.setIdUser(transaction.getIdUser());
        return record;
    }

}
