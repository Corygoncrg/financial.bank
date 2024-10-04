package com.example.financial.transactions.model;

import java.time.LocalDateTime;

public class TransactionRecordToCsv {

    public static TransactionCsv transactionAdapter(TransactionCsvRecord csvRecord, LocalDateTime importDate) {
        TransactionCsv transaction = new TransactionCsv();
        transaction.setOriginalBank(csvRecord.getOriginalBank());
        transaction.setOriginalAgency(csvRecord.getOriginalAgency());
        transaction.setOriginalAccount(csvRecord.getOriginalAccount());
        transaction.setDestinyBank(csvRecord.getDestinyBank());
        transaction.setDestinyAgency(csvRecord.getDestinyAgency());
        transaction.setDestinyAccount(csvRecord.getDestinyAccount());
        transaction.setAmount(csvRecord.getAmount());
        transaction.setTransactionTime(csvRecord.getTransactionTime());
        transaction.setImportDate(importDate);
        return transaction;
    }
}
