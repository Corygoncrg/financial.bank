package com.example.transactions.util;

public class SqlFields {

    public static final String[] listFields = {"original.bank", "original.agency", "original.account", "destiny.bank", "destiny.agency", "destiny.account", "amount", "transaction_date"};
    public static final String allFields = "original_bank, original_agency, original_account, destiny_bank, destiny_agency, destiny_account, amount, transaction_date, import_date, id_user";
    public static final String allJavaFields = ":originalBank, :originalAgency, :originalAccount, :destinyBank, :destinyAgency, :destinyAccount, :amount, :transactionDate, :importDate, :userId";
}
