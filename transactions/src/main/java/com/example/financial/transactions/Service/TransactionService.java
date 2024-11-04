package com.example.financial.transactions.Service;

import com.example.financial.transactions.kafka.KafkaResponseHandler;
import com.example.financial.transactions.model.TransactionAdapter;
import com.example.financial.transactions.model.TransactionCsv;
import com.example.financial.transactions.model.TransactionCsvRecord;
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
    private KafkaTemplate<String, String> kafkaTemplate;


    @Autowired
    private KafkaResponseHandler responseHandler;

    public Resource getResourceResponseEntity(String filename, StorageService storageService) {
        return storageService.loadAsResource(filename);
    }

    public List<TransactionCsvRecord> getTransactionsFromFiles(StorageService storageService) throws IOException {
        List<String> fileNames = storageService.loadAll()
                .map(Path::toString)
                .toList();

        Set<LocalDateTime> transactionDates = new HashSet<>();

        // For each file, extract the transaction dates
        for (String fileName : fileNames) {
            List<String> fileContent = storageService.loadFileContent(fileName);
            transactionDates.addAll(csvParserService.extractTransactionDatesFromFile(fileContent));
        }

        // Query the database for transactions with matching dates
        List<TransactionCsv> transactions = repository.findByTransactionDateIn(transactionDates);

        // Map TransactionCsv to TransactionCsvRecord
        return transactions.stream().map(TransactionAdapter::transactionCsvToRecordAdapter).collect(Collectors.toList());
    }

    public void csvFileUpload(MultipartFile csvFile, String token, RedirectAttributes redirectAttributes, JobLauncher jobLauncher,
                              Job importTransactionJob, StorageService storageService) throws JsonProcessingException {
        kafkaTemplate.send("FINANCIAL_BANK_TRANSACTIONS", token);
        Long userId;
        try {
            // Wait for the user ID from Kafka with a timeout
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
            return; // Exit the method if user ID could not be retrieved
        }
        var userDto = responseHandler.getUserDto();
        ObjectMapper objectMapper = new ObjectMapper();
        String filename = storageService.store(csvFile);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", filename)
                .addString("userDto", objectMapper.writeValueAsString(userDto))
                .addLong("time", System.currentTimeMillis())  // Use time to ensure uniqueness
                .toJobParameters();
        try {
            jobLauncher.run(importTransactionJob, jobParameters);
            redirectAttributes.addFlashAttribute("message", "Successfully uploaded and processed " + csvFile.getOriginalFilename() + "!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to process file: " + e.getMessage());
        }
    }


}