package com.example.financial.transactions.model;

import java.time.LocalDateTime;

public class TransactionAdapter {

    public static TransactionCsv transactionRecordToCsvAdapter(TransactionCsvRecord csvRecord, LocalDateTime importDate) {
        TransactionCsv transaction = new TransactionCsv();
        transaction.setOriginalBank(csvRecord.getOriginalBank());
        transaction.setOriginalAgency(csvRecord.getOriginalAgency());
        transaction.setOriginalAccount(csvRecord.getOriginalAccount());
        transaction.setDestinyBank(csvRecord.getDestinyBank());
        transaction.setDestinyAgency(csvRecord.getDestinyAgency());
        transaction.setDestinyAccount(csvRecord.getDestinyAccount());
        transaction.setAmount(csvRecord.getAmount());
        transaction.setTransactionDate(csvRecord.getTransactionDate());
        transaction.setImportDate(importDate);
        return transaction;
    }

    public static TransactionCsvRecord transactionCsvToRecordAdapter(TransactionCsv transaction) {
        TransactionCsvRecord record = new TransactionCsvRecord();
        record.setOriginalBank(transaction.getOriginalBank());
        record.setOriginalAgency(transaction.getOriginalAgency());
        record.setOriginalAccount(transaction.getOriginalAccount());
        record.setDestinyBank(transaction.getDestinyBank());
        record.setDestinyAgency(transaction.getDestinyAgency());
        record.setDestinyAccount(transaction.getDestinyAccount());
        record.setAmount(transaction.getAmount());
        record.setTransactionDate(transaction.getTransactionDate());
        record.setImportDate(transaction.getImportDate());
        return record;
    }
}
