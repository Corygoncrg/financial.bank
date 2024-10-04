package com.example.financial.transactions.Service;

public class SqlService {

    public static final String[] listFields = {"original_bank", "original_agency", "original_account", "destiny_bank", "destiny_agency", "destiny_account", "amount", "transaction_time"};
    public static final String allFields = "original_bank, original_agency, original_account, destiny_bank, destiny_agency, destiny_account, amount, transaction_time, import_date";
    public static final String allJavaFields = ":originalBank, :originalAgency, :originalAccount, :destinyBank, :destinyAgency, :destinyAccount, :amount, :transactionTime, :importDate";
}
