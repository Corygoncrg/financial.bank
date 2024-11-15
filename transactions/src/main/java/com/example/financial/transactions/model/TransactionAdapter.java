package com.example.financial.transactions.model;

import java.time.LocalDateTime;

public class TransactionAdapter {

    public static Transaction transactionRecordToTransactionAdapter(TransactionRecord record, LocalDateTime importDate) {
        Transaction transaction = new Transaction();
        transaction.setOriginalBank(record.getOriginal().getBank());
        transaction.setOriginalAgency(record.getOriginal().getAgency());
        transaction.setOriginalAccount(record.getOriginal().getAccount());
        transaction.setDestinyBank(record.getDestiny().getBank());
        transaction.setDestinyAgency(record.getDestiny().getAgency());
        transaction.setDestinyAccount(record.getDestiny().getAccount());
        transaction.setAmount(record.getAmount());
        transaction.setTransactionDate(record.getTransactionDate());
        transaction.setImportDate(importDate);
        transaction.setIdUser(record.getIdUser());
        return transaction;
    }

}
