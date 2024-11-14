package com.example.financial.transactions.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@XmlRootElement(name = "transacao")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionRecord {
//FIXME The XmlPaths are not finding the fields correctly


    @XmlElement(name = "banco")
    @XmlPath("origem/banco/text()")
    private String originalBank;

    @XmlElement(name = "agencia")
    @XmlPath("origem/agencia/text()")
    private String originalAgency;

    @XmlElement(name = "conta")
    @XmlPath("origem/conta/text()")
    private String originalAccount;

    @XmlElement(name = "banco")
    @XmlPath("destino/banco/text()")
    private String destinyBank;

    @XmlElement(name = "agencia")
    @XmlPath("destino/agencia/text()")
    private String destinyAgency;

    @XmlElement(name = "conta")
    @XmlPath("destino/conta/text()")
    private String destinyAccount;

    @XmlElement(name = "valor")
    private BigDecimal amount;

    @XmlElement(name = "data")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime transactionDate;

    private LocalDateTime importDate;
    private User idUser;
}
