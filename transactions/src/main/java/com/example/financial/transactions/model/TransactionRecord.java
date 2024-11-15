package com.example.financial.transactions.model;

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
@XmlRootElement(name = "transacao")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionRecord {


    @XmlElement(name = "origem")
    private BankDetails original;

    @XmlElement(name = "destino")
    private BankDetails destiny;

    @XmlElement(name = "valor")
    private BigDecimal amount;

    @XmlElement(name = "data")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime transactionDate;

    private LocalDateTime importDate;
    private User idUser;

    @Data
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class BankDetails {
        @XmlElement(name = "banco")
        private String bank;

        @XmlElement(name = "agencia")
        private String agency;

        @XmlElement(name = "conta")
        private String account;
    }

}
