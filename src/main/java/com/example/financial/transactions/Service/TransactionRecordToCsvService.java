package com.example.financial.transactions.Service;

import com.example.financial.transactions.model.TransactionCsv;
import com.example.financial.transactions.model.TransactionCsvRecord;
import org.springframework.stereotype.Service;

@Service
public class TransactionRecordToCsvService {

    public static TransactionCsv transactionAdapter(TransactionCsvRecord csvRecord) {
        TransactionCsv transaction = new TransactionCsv();
        transaction.setOriginalBank(csvRecord.getOriginalBank());
        transaction.setOriginalAgency(csvRecord.getOriginalAgency());
        transaction.setOriginalAccount(csvRecord.getOriginalAccount());
        transaction.setDestinyBank(csvRecord.getDestinyBank());
        transaction.setDestinyAgency(csvRecord.getDestinyAgency());
        transaction.setDestinyAccount(csvRecord.getDestinyAccount());
        transaction.setAmount(csvRecord.getAmount());
        transaction.setTransactionTime(csvRecord.getTransactionTime());
        return transaction;
    }
}
