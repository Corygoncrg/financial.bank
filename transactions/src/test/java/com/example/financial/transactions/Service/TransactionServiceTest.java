package com.example.financial.transactions.Service;

import com.example.financial.transactions.dto.TransactionDto;
import com.example.financial.transactions.dto.UserDto;
import com.example.financial.transactions.kafka.KafkaResponseHandler;
import com.example.financial.transactions.model.LocalDateTimeEditor;
import com.example.financial.transactions.model.Transaction;
import com.example.financial.transactions.model.User;
import com.example.financial.transactions.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings("OctalInteger")
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

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    JobLauncher jobLauncher;

    @Mock
    RedirectAttributes redirectAttributes;

    @Mock
    MultipartFile csvFile;

    @Mock
    Job importTransactionJob;

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
    @DisplayName("Test the file names are loaded correctly")
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
    @DisplayName("Test file content is loaded correctly")
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
        List<LocalDateTime> list = List.of(getTransaction().getTransactionDate(),
                getTransaction2().getTransactionDate());
        when(csvParserService.extractTransactionDatesFromFile(content)).thenReturn(list);

        var result = csvParserService.extractTransactionDatesFromFile(content);

        assertEquals(list, result);
    }

    @Test
    @DisplayName("Test xml parsing from transaction date")
    void getTransactionFromFiles4() {
        List<String> content = List.of("name", "birthday", "age", "phone");
        List<LocalDateTime> list = List.of(getTransaction().getTransactionDate(),
                getTransaction2().getTransactionDate());
        when(xmlParserService.extractTransactionDatesFromFile(content)).thenReturn(list);

        var result = xmlParserService.extractTransactionDatesFromFile(content);

        assertEquals(list, result);
    }

    @Test
    @DisplayName("Test repository returns expected transactions")
    void getTransactionFromFiles5() {
        var transactionDate = getTransaction().getTransactionDate();
        var transactionDate2 = getTransaction2().getTransactionDate();
        Set<LocalDateTime> transactionDates = Set.of(transactionDate, transactionDate2);
        Transaction transaction = getTransaction();
        var transaction2 = getTransaction2();

        List<Transaction> transactions = List.of(transaction, transaction2);
        when(repository.findByTransactionDateIn(transactionDates)).thenReturn(transactions);

        var result = repository.findByTransactionDateIn(transactionDates);

        assertEquals(transactions, result);
    }

    @Test
    @DisplayName("Test transaction DTOs are returned from files")
    void getTransactionsFromFilesTest() throws IOException {
        // Arrange
        List<String> fileNames = List.of("file1.csv", "file2.xml");
        List<String> csvContent = List.of("csvData1", "csvData2");
        List<String> xmlContent = List.of("xmlData1", "xmlData2");
        var transactionDate = getTransaction().getTransactionDate();
        var transactionDate2 = getTransaction2().getTransactionDate();
        List<LocalDateTime> transactionDates = List.of(transactionDate,
                transactionDate2);
        Set<LocalDateTime> transactionDateSet = Set.of(transactionDate, transactionDate2);

        List<Transaction> transactions = List.of(getTransaction(), getTransaction2());

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
    @DisplayName("Test JobParameters are set")
    void csvFileUpload3() throws JsonProcessingException {
        String filename = "filename";
        var userDto = getUserDto();
        var time = System.currentTimeMillis();
        var objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsString(userDto)).thenReturn(getUserDtoJson());

        var result = new JobParametersBuilder()
                .addString("filename", filename)
                .addString("userDto", objectMapper.writeValueAsString(userDto))
                .addLong("time", time)
                .toJobParameters();

        assertEquals(filename, result.getString("filename"));
        assertEquals(getUserDtoJson(), result.getString("userDto"));
        assertEquals(time, result.getLong("time"));
    }

    @Test
    @DisplayName("Test kafka message is sent")
    void csvFileUpload4() throws JsonProcessingException, InterruptedException {
        String token = "Magic token";
        String testFile = "testFile.csv";
        when(storageService.store(csvFile)).thenReturn(testFile);
        when(responseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)).thenReturn(true);
        when(responseHandler.getUserDto()).thenReturn(getUserDto());
        transactionService.csvFileUpload(csvFile, token, redirectAttributes, jobLauncher, importTransactionJob, storageService);

        verify(kafkaTemplate).send("FINANCIAL_BANK_TRANSACTIONS", token);
    }

    @Test
    @DisplayName("Test timeout while waiting for User Id")
    void csvFileUpload5() throws JsonProcessingException, InterruptedException {
        String token = "Magic token";

        when(responseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)).thenReturn(false);

        transactionService.csvFileUpload(csvFile, token, redirectAttributes, jobLauncher, importTransactionJob, storageService);

        verify(redirectAttributes).addFlashAttribute("message", "Failed to retrieve user ID: Timeout while waiting for user ID from Kafka");
    }

    @Test
    @DisplayName("Test file storage and filename in jobParameters")
    void csvFileUpload6() throws JsonProcessingException, InterruptedException {
        String token = "Magic token";

        String testFile = "testFile.csv";
        when(storageService.store(csvFile)).thenReturn(testFile);
        when(responseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)).thenReturn(true);
        when(responseHandler.getUserDto()).thenReturn(getUserDto());


        transactionService.csvFileUpload(csvFile, token, redirectAttributes, jobLauncher, importTransactionJob, storageService);

        verify(storageService).store(csvFile);
    }

    @Test
    @DisplayName("Test successful file upload")
    void csvFileUpload7() throws JsonProcessingException, InterruptedException {
        String token = "Magic token";

        String testFile = "testFile.csv";

        when(storageService.store(csvFile)).thenReturn(testFile);
        when(responseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)).thenReturn(true);
        when(responseHandler.getUserDto()).thenReturn(getUserDto());
        when(csvFile.getOriginalFilename()).thenReturn(testFile);


        transactionService.csvFileUpload(csvFile, token, redirectAttributes, jobLauncher, importTransactionJob, storageService);

        verify(redirectAttributes).addFlashAttribute("message", "Successfully uploaded and processed " + testFile + "!");
    }

    @Test
    @DisplayName("Test overall CSV/XMl file upload flow")
    void testCsvFileUploadIntegration() throws Exception {
        String token = "someToken";
        String filename = "testFile.csv";
        when(storageService.store(csvFile)).thenReturn(filename);
        when(responseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)).thenReturn(true);
        when(responseHandler.getUserDto()).thenReturn(getUserDto());

        transactionService.csvFileUpload(csvFile, token, redirectAttributes, jobLauncher, importTransactionJob, storageService);

        verify(kafkaTemplate).send("FINANCIAL_BANK_TRANSACTIONS", token);
        verify(storageService).store(csvFile);
        verify(jobLauncher).run(eq(importTransactionJob), any(JobParameters.class));
        verify(redirectAttributes).addFlashAttribute(
                "message", "Successfully uploaded and processed " + csvFile.getOriginalFilename() + "!"
        );
    }

    @Test
    @DisplayName("Test transactions are loaded correctly")
    void getTransactionsByImportDate() {
        String importDate = "2022/01";
        var localDateTimeEditor = mock(LocalDateTimeEditor.class);
        List<Transaction> transactions = List.of(getTransaction(), getTransaction2());
        localDateTimeEditor.setAsText(importDate);
        LocalDateTime localDateTime = (LocalDateTime) localDateTimeEditor.getValue();
        when(repository.findByImportDate(localDateTime)).thenReturn(transactions);

        var result = repository.findByImportDate(localDateTime);

        assertEquals(transactions, result);
    }

    @Test
    @DisplayName("Test suspected transactions are loaded correctly")
    void getSuspectTransactionsByYearAndMonth() {
        int year = 2022;
        int month = 01;
        List<Transaction> transactions = List.of(getTransaction(), getTransaction2());

        when(repository.findSuspectTransactionsByYearAndMonth(year, month)).thenReturn(transactions);

        var result = repository.findSuspectTransactionsByYearAndMonth(year, month);

        assertEquals(transactions, result);

    }

    @Test
    @DisplayName("Test suspected accounts are loaded correctly")
    void getSuspectAccountsByYearAndMonth() {
        int year = 2022;
        int month = 01;
        List<Object[]> transactions = List.of(
                new Object[]{1L, "Transaction1", 100.0},
                new Object[]{2L, "Transaction2", 200.0}
        );
        when(repository.findSuspectAccountsByYearAndMonth(year, month)).thenReturn(transactions);

        var result = repository.findSuspectAccountsByYearAndMonth(year, month);

        assertEquals(transactions, result);
        assertEquals(1L, transactions.get(0)[0]);
        assertEquals("Transaction1", transactions.get(0)[1]);
        assertEquals(100.0, transactions.get(0)[2]);

    }

    @Test
    @DisplayName("Test suspected agencies are loaded correctly")
    void getSuspectAgenciesByYearAndMonth() {
        int year = 2022;
        int month = 01;
        List<Object[]> transactions = List.of(
                new Object[]{1L, "Transaction1", 100.0},
                new Object[]{2L, "Transaction2", 200.0}
        );
        when(repository.findSuspectAgenciesByYearAndMonth(year, month)).thenReturn(transactions);

        var result = repository.findSuspectAgenciesByYearAndMonth(year, month);

        assertEquals(transactions, result);
        assertEquals(1L, transactions.get(0)[0]);
        assertEquals("Transaction1", transactions.get(0)[1]);
        assertEquals(100.0, transactions.get(0)[2]);

    }

    private Transaction getTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setOriginalBank("originalBank");
        transaction.setOriginalAgency("originalAgency");
        transaction.setOriginalAccount("originalAccount");
        transaction.setDestinyBank("destinyBank");
        transaction.setDestinyAgency("destinyAgency");
        transaction.setDestinyAccount("destinyAccount");
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setTransactionDate(LocalDateTime.of(2020, 2, 1, 12, 0));
        transaction.setIdUser(user);
        return transaction;
    }
    private Transaction getTransaction2() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setOriginalBank("originalBank2");
        transaction.setOriginalAgency("originalAgency2");
        transaction.setOriginalAccount("originalAccount2");
        transaction.setDestinyBank("destinyBank2");
        transaction.setDestinyAgency("destinyAgency2");
        transaction.setDestinyAccount("destinyAccount2");
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setTransactionDate(LocalDateTime.of(2020, 2, 1, 13, 30));
        transaction.setIdUser(user);
        return transaction;
    }

    private UserDto getUserDto() {
        return new UserDto(1L, "Name", "example@email.com", "Active");
    }

    private String getUserDtoJson() {
        return "{\"id\":1,\"name\":\"Name\",\"email\":\"example@email.com\",\"status\":\"Active\"}";
    }

}