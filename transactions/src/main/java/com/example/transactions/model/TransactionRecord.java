package com.example.transactions.model;

import com.example.shared.model.User;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@XmlRootElement(name = "transaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionRecord {


    @XmlElement(name = "origin")
    private BankDetails original;

    @XmlElement(name = "destination")
    private BankDetails destiny;

    @XmlElement(name = "amount")
    private BigDecimal amount;

    @XmlElement(name = "transactionDate")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime transactionDate;

    private LocalDateTime importDate;
    private User idUser;

    @Data
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class BankDetails {
        @XmlElement(name = "bank")
        private String bank;

        @XmlElement(name = "agency")
        private String agency;

        @XmlElement(name = "account")
        private String account;
    }

}
