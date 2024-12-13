package com.example.transactions.dto;

import java.math.BigDecimal;

public record AccountDto(String bank, String agency, String account, BigDecimal totalAmountMoved, String transferType)
{
    public static AccountDto from(Object[] result) {
        return new AccountDto(
                (String) result[0], // bank
                (String) result[1], // agency
                (String) result[2], // account
                (BigDecimal) result[3], // totalAmountMoved
                (String) result[4]  // transferType
        );
    }}
