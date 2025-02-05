package com.example.transactions.Service;

import com.example.shared.dto.UserDto;
import com.example.shared.model.User;
import com.example.transactions.model.Transaction;
import com.example.transactions.model.TransactionRecord;
import com.example.transactions.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TransactionBatchServiceTest {

    @Mock
    TransactionRepository repository;

    @Mock
    private TransactionRecord record;

    @InjectMocks
    TransactionBatchService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test duplicate returns null")
    void processRecord() {
        var transactionDate = LocalDateTime.of(2022, 7, 1, 12, 0);
        when(record.getTransactionDate()).thenReturn(transactionDate);
        when(repository.existsByTransactionDate(transactionDate)).thenReturn(true);


       var result = service.processRecord(record, LocalDateTime.now(), getUserDto());


        assertNull(result);

    }
    @Test
    @DisplayName("Test non-duplicate returns not null")
    void processRecord2() {
        var transactionDate = LocalDateTime.of(2022, 7, 1, 12, 0);

        when(record.getTransactionDate()).thenReturn(transactionDate);
        when(repository.existsByTransactionDate(transactionDate)).thenReturn(false);
        when(record.getOriginal()).thenReturn(originalDetails());
        when(record.getDestiny()).thenReturn(destinyDetails());

       var result = service.processRecord(record, LocalDateTime.now(), getUserDto());

        assertNotNull(result);
    }

    @Test
    @DisplayName("Test follow-up transaction of a different date")
    void testTransactionWithDifferentDate() {
        LocalDateTime newTransactionDate = LocalDateTime.of(2024, 6, 29, 12, 0);


        var user = new User(getUserDto());
        var importDate = LocalDateTime.now();
        var transactionDate = LocalDateTime.of(2022, 7, 1, 12, 0);
        var expected = transactionConstructor(transactionDate, importDate, user);

        when(record.getTransactionDate()).thenReturn(transactionDate);
        when(repository.existsByTransactionDate(transactionDate)).thenReturn(false);
        when(record.getOriginal()).thenReturn(originalDetails());
        when(record.getDestiny()).thenReturn(destinyDetails());
        when(record.getIdUser()).thenReturn(user);

        var result = service.processRecord(record, importDate, getUserDto());

        assertEquals(expected,result);
        assertEquals(expected.getUserId(), result.getUserId());

        when(record.getTransactionDate()).thenReturn(newTransactionDate);
        when(repository.existsByTransactionDate(newTransactionDate)).thenReturn(true); // Simulate duplicate
        var secondResult= service.processRecord(record, importDate, getUserDto());

        assertNull(secondResult, "Skipping transaction with date: " + newTransactionDate);

    }

    @Test
    @DisplayName("Test idUser is set correctly")
    void testIdUserIsSet() {
        var user = new User(getUserDto());
        var importDate = LocalDateTime.now();
        var transactionDate = LocalDateTime.of(2022, 7, 1, 12, 0);
        var expected = transactionConstructor(transactionDate, importDate, user);

        when(record.getTransactionDate()).thenReturn(transactionDate);
        when(repository.existsByTransactionDate(transactionDate)).thenReturn(false);
        when(record.getOriginal()).thenReturn(originalDetails());
        when(record.getDestiny()).thenReturn(destinyDetails());
        when(record.getIdUser()).thenReturn(user);

        var result = service.processRecord(record, importDate, getUserDto());

        assertEquals(expected,result);
        assertEquals(expected.getUserId(), result.getUserId());
    }

    private Transaction transactionConstructor(LocalDateTime transactionDate, LocalDateTime importDate, User user) {
        Transaction transaction = new Transaction();
        transaction.setOriginalBank(originalDetails().getBank());
        transaction.setOriginalAgency(originalDetails().getAgency());
        transaction.setOriginalAccount(originalDetails().getAccount());
        transaction.setDestinyBank((destinyDetails()).getBank());
        transaction.setDestinyAgency((destinyDetails().getAgency()));
        transaction.setDestinyAccount((destinyDetails().getAccount()));
        transaction.setTransactionDate(transactionDate);
        transaction.setImportDate(importDate);
        transaction.setIdUser(user);
        return transaction;
    }

    TransactionRecord.BankDetails originalDetails() {
         var bank = new TransactionRecord.BankDetails();
         bank.setBank("bank");
         bank.setAccount("account");
         bank.setAgency("agency");
        return bank;
    }
    TransactionRecord.BankDetails destinyDetails() {
         var bank = new TransactionRecord.BankDetails();
         bank.setBank("bank-2");
         bank.setAccount("account-2");
         bank.setAgency("agency-2");
        return bank;
    }

    private UserDto getUserDto() {
        List<String> list = List.of("element1", "element2");
        return new UserDto(1L, "Name", "example@email.com", "password", "ACTIVE", list);
    }

}