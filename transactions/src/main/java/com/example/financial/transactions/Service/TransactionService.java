package com.example.financial.transactions.Service;

import com.example.financial.transactions.dto.AccountDto;
import com.example.financial.transactions.dto.AgencyDto;
import com.example.financial.transactions.dto.TransactionDto;
import com.example.financial.transactions.kafka.KafkaResponseHandler;
import com.example.financial.transactions.model.LocalDateTimeEditor;
import com.example.financial.transactions.model.Transaction;
import com.example.financial.transactions.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private CsvParserService csvParserService;

    @Autowired
    private XmlParserService xmlParserService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final String topic = "FINANCIAL_BANK_TRANSACTIONS_REQUEST";

    private final LocalDateTimeEditor localDateTimeEditor;

    @Autowired
    public TransactionService() {
        this.localDateTimeEditor = new LocalDateTimeEditor("yyyy-MM-dd'T'HH:mm:ss");
    }

    @Autowired
    private KafkaResponseHandler responseHandler;

    public Resource getResourceResponseEntity(String filename, StorageService storageService) {
        return storageService.loadAsResource(filename);
    }

    public List<TransactionDto> getTransactionsFromFiles(StorageService storageService) throws IOException {
        List<String> fileNames = storageService.loadAll()
                .map(Path::toString)
                .toList();

        Set<LocalDateTime> transactionDates = new HashSet<>();

        for (String fileName : fileNames) {
            if (fileName.endsWith(".csv")) {
                List<String> fileContent = storageService.loadFileContent(fileName);
                transactionDates.addAll(csvParserService.extractTransactionDatesFromFile(fileContent));
            } else if (fileName.endsWith(".xml")) {
                List<String> fileContent = storageService.loadFileContent(fileName);
                transactionDates.addAll(xmlParserService.extractTransactionDatesFromFile(fileContent));
            }
        }

        List<Transaction> transactions = repository.findByTransactionDateIn(transactionDates);

        return transactions.stream().map(TransactionDto::from).collect(Collectors.toList());
    }

    public void csvFileUpload(MultipartFile csvFile, String token, RedirectAttributes redirectAttributes, JobLauncher jobLauncher,
                              Job importTransactionJobCsv, StorageService storageService) throws JsonProcessingException {
        kafkaTemplate.send(topic, token);
        Long userId;
        try {
            if (responseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)) {
                userId = responseHandler.getUserDto().id();

                if (userId == null) {
                    throw new RuntimeException("User ID retrieval failed: received null");
                }
            } else {
                throw new RuntimeException("Timeout while waiting for user ID from Kafka");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to retrieve user ID: " + e.getMessage());
            return;
        }
        var userDto = responseHandler.getUserDto();
        ObjectMapper objectMapper = new ObjectMapper();
        String filename = storageService.store(csvFile);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", filename)
                .addString("userDto", objectMapper.writeValueAsString(userDto))
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        try {
            jobLauncher.run(importTransactionJobCsv, jobParameters);
            redirectAttributes.addFlashAttribute("message", "Successfully uploaded and processed " + csvFile.getOriginalFilename() + "!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to process file: " + e.getMessage());
        }
    }

    public void xmlFileUpload(MultipartFile xmlFile, String token, RedirectAttributes redirectAttributes, JobLauncher jobLauncher,
                              Job importTransactionJobXml, StorageService storageService) throws JsonProcessingException {
        kafkaTemplate.send(topic, token);
        Long userId;
        try {
            if (responseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)) {
                userId = responseHandler.getUserDto().id();

                if (userId == null) {
                    throw new RuntimeException("User ID retrieval failed: received null");
                }
            } else {
                throw new RuntimeException("Timeout while waiting for user ID from Kafka");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to retrieve user ID: " + e.getMessage());
            return;
        }
        var userDto = responseHandler.getUserDto();
        String filename = storageService.store(xmlFile);
        ObjectMapper objectMapper = new ObjectMapper();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", filename)
                .addString("userDto", objectMapper.writeValueAsString(userDto))
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(importTransactionJobXml, jobParameters);
            redirectAttributes.addFlashAttribute("message", "Successfully uploaded and processed " + xmlFile.getOriginalFilename() + "!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to process file: " + e.getMessage());
        }
    }

    public List<TransactionDto> getTransactionsByImportDate(String importDate) {
        localDateTimeEditor.setAsText(importDate);
        LocalDateTime localDateTime = (LocalDateTime) localDateTimeEditor.getValue();
        var transactions = repository.findByImportDate(localDateTime);
        return transactions.stream().map(TransactionDto::from).collect(Collectors.toList());
    }

    public List<TransactionDto> getSuspectTransactionsByYearAndMonth(int year, int month) {
        var transactions = repository.findSuspectTransactionsByYearAndMonth(year, month);

        return transactions.stream().map(TransactionDto::from).collect(Collectors.toList());
    }

    public List<AccountDto> getSuspectAccountsByYearAndMonth(int year, int month) {
        List<Object[]> results = repository.findSuspectAccountsByYearAndMonth(year, month);
        return results.stream().map(AccountDto::from).collect(Collectors.toList());
    }

    public List<AgencyDto> getSuspectAgenciesByYearAndMonth(int year, int month) {
        List<Object[]> results = repository.findSuspectAgenciesByYearAndMonth(year, month);
        return results.stream().map(AgencyDto::from).collect(Collectors.toList());
    }


}