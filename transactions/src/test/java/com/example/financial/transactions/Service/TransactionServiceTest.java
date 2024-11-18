package com.example.financial.transactions.Service;

import com.example.financial.transactions.dto.TransactionDto;
import com.example.financial.transactions.dto.UserDto;
import com.example.financial.transactions.kafka.KafkaResponseHandler;
import com.example.financial.transactions.model.Transaction;
import com.example.financial.transactions.model.User;
import com.example.financial.transactions.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    StorageService storageService;

    @Mock
    Resource resource;

    @Mock
    CsvParserService csvParserService;

    @Mock
    XmlParserService xmlParserService;

    @Mock
    TransactionRepository repository;

    @Mock
    User user;


    @Mock
    KafkaResponseHandler responseHandler;

    @InjectMocks
    TransactionService transactionService;

    @Test
    @DisplayName("Test resource returns filename")
    void getResourceResponseEntity() {
        //arrange
        String filename = "file";
        when(resource.getFilename()).thenReturn(filename);
        when(storageService.loadAsResource(filename)).thenReturn(resource);

        //act
        var result = storageService.loadAsResource(filename);

        //assert
        assertEquals("file", result.getFilename());

    }

    @Test
    @DisplayName("Test that the file names are loaded correctly")
    void getTransactionsFromFiles1() {
        //arrange
        List<String> fileNames = List.of("file1.csv", "file2.xml", "file3.csv");
        when(storageService.loadAll()).thenReturn(fileNames.stream().map(Paths::get));

        //act
        var result = storageService.loadAll().map(Path::toString).toList();
        //assert

        assertEquals(fileNames, result);
    }

    @Test
    @DisplayName("Test that file content is loaded correctly")
    void getTransactionFromFiles2() throws IOException {
        List<String> content = List.of("name", "birthday", "age", "phone");
        String filename = "fileName";
        when(storageService.loadFileContent(filename)).thenReturn(content);

        var result = storageService.loadFileContent(filename);

        assertEquals(content, result);
    }

    @Test
    @DisplayName("Test csv parsing from transaction date")
    void getTransactionFromFiles3() {
        List<String> content = List.of("name", "birthday", "age", "phone");
        List<LocalDateTime> list = List.of(LocalDateTime.of(2020, 2, 1, 12, 0),
                                     LocalDateTime.of(2022, 2, 1, 12, 0));
        when(csvParserService.extractTransactionDatesFromFile(content)).thenReturn(list);

        var result = csvParserService.extractTransactionDatesFromFile(content);

        assertEquals(list, result);
    }

    @Test
    @DisplayName("Test xml parsing from transaction date")
    void getTransactionFromFiles4() {
        List<String> content = List.of("name", "birthday", "age", "phone");
        List<LocalDateTime> list = List.of(LocalDateTime.of(2020, 2, 1, 12, 0),
                                     LocalDateTime.of(2020, 2, 1, 12, 0));
        when(xmlParserService.extractTransactionDatesFromFile(content)).thenReturn(list);

        var result = xmlParserService.extractTransactionDatesFromFile(content);

        assertEquals(list, result);
    }

    @Test
    @DisplayName("Test repository returns expected transactions")
    void getTransactionFromFiles5() {
        var decimal = BigDecimal.valueOf(1000);
        var transactionDate = getTransaction(decimal).getTransactionDate();
        var transactionDate2 = getTransaction2(decimal).getTransactionDate();
        Set<LocalDateTime> transactionDates = Set.of(transactionDate, transactionDate2);
        Transaction transaction = getTransaction(decimal);
        var transaction2 = getTransaction2(decimal);

        List<Transaction> transactions = List.of(transaction, transaction2);
        when(repository.findByTransactionDateIn(transactionDates)).thenReturn(transactions);

        var result = repository.findByTransactionDateIn(transactionDates);

        assertEquals(transactions, result);
    }

    @Test
    @DisplayName("Test that transaction DTOs are returned from files")
    void getTransactionsFromFilesTest() throws IOException {
        // Arrange
        List<String> fileNames = List.of("file1.csv", "file2.xml");
        List<String> csvContent = List.of("csvData1", "csvData2");
        List<String> xmlContent = List.of("xmlData1", "xmlData2");
        var decimal = BigDecimal.valueOf(1000);
        var transactionDate = getTransaction(decimal).getTransactionDate();
        var transactionDate2 = getTransaction2(decimal).getTransactionDate();
        List<LocalDateTime> transactionDates = List.of(LocalDateTime.of(2020, 2, 1, 12, 0),
                LocalDateTime.of(2020, 2, 1, 13, 30));
        Set<LocalDateTime> transactionDateSet = Set.of(transactionDate, transactionDate2);

        List<Transaction> transactions = List.of(getTransaction(decimal), getTransaction2(decimal));

        when(storageService.loadAll()).thenReturn(fileNames.stream().map(Paths::get));
        when(storageService.loadFileContent("file1.csv")).thenReturn(csvContent);
        when(storageService.loadFileContent("file2.xml")).thenReturn(xmlContent);
        when(csvParserService.extractTransactionDatesFromFile(csvContent)).thenReturn(transactionDates);
        when(xmlParserService.extractTransactionDatesFromFile(xmlContent)).thenReturn(transactionDates);
        when(repository.findByTransactionDateIn(transactionDateSet)).thenReturn(transactions);

        // Act
        List<TransactionDto> result = transactionService.getTransactionsFromFiles(storageService);

        // Assert
        assertEquals(transactions.size(), result.size());
        assertEquals(TransactionDto.from(transactions.get(0)), result.get(0));
    }

    @Test
    @DisplayName("Test responseHandler returns id")
    void csvFileUpload1() {
        var userId = 1L;

        when(responseHandler.getUserDto()).thenReturn(getUserDto());

        var result = responseHandler.getUserDto().id();

        assertEquals(userId, result);
    }

    @Test
    @DisplayName("Test responseHandler returns UserDto")
    void csvFileUpload2() {
        when(responseHandler.getUserDto()).thenReturn(getUserDto());

        var result = responseHandler.getUserDto();

        assertEquals(getUserDto(), result);
    }

    @Test
    @DisplayName("")
    void csvFileUpload3() {
/*
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", filename)
                .addString("userDto", objectMapper.writeValueAsString(userDto))
                .addLong("time", System.currentTimeMillis())  // Use time to ensure uniqueness
                .toJobParameters();
*/

    }


    private Transaction getTransaction(BigDecimal decimal) {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setOriginalBank("originalBank");
        transaction.setOriginalAgency("originalAgency");
        transaction.setOriginalAccount("originalAccount");
        transaction.setDestinyBank("destinyBank");
        transaction.setDestinyAgency("destinyAgency");
        transaction.setDestinyAccount("destinyAccount");
        transaction.setAmount(decimal);
        transaction.setTransactionDate(LocalDateTime.of(2020, 2, 1, 12, 0));
        transaction.setIdUser(user);
        return transaction;
    }
    private Transaction getTransaction2(BigDecimal decimal) {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setOriginalBank("originalBank2");
        transaction.setOriginalAgency("originalAgency2");
        transaction.setOriginalAccount("originalAccount2");
        transaction.setDestinyBank("destinyBank2");
        transaction.setDestinyAgency("destinyAgency2");
        transaction.setDestinyAccount("destinyAccount2");
        transaction.setAmount(decimal);
        transaction.setTransactionDate(LocalDateTime.of(2020, 2, 1, 13, 30));
        transaction.setIdUser(user);
        return transaction;
    }

    private UserDto getUserDto() {
        return new UserDto(1L, "Name", "example@email.com", "Active");
    }

}