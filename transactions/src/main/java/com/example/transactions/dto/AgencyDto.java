package com.example.transactions.dto;

import java.math.BigDecimal;

public record AgencyDto(String bank, String agency, BigDecimal totalAmountMoved, String transferType)
{

    public static AgencyDto from(Object[] result) {
        return new AgencyDto(
                (String) result[0], // bank
                (String) result[1], // agency
                (BigDecimal) result[2], // totalAmountMoved
                (String) result[3]  // transferType
        );
    }

}
