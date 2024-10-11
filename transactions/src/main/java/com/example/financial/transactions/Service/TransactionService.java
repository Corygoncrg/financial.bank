package com.example.financial.transactions.Service;

import com.example.financial.transactions.model.TransactionAdapter;
import com.example.financial.transactions.model.TransactionCsv;
import com.example.financial.transactions.model.TransactionCsvRecord;
import com.example.financial.transactions.repository.TransactionRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private CsvParserService csvParserService;

    public  ResponseEntity<Resource> getResourceResponseEntity(String filename, StorageService storageService) {
        Resource file = storageService.loadAsResource(filename);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
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

    public void csvFileUpload(MultipartFile csvFile, RedirectAttributes redirectAttributes, JobLauncher jobLauncher, Job importTransactionJob, StorageService storageService) {
        String filename = storageService.store(csvFile);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", filename)
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