package com.example.financial.transactions.controller;

import com.example.financial.transactions.Service.StorageService;
import com.example.financial.transactions.Service.TransactionService;
import com.example.financial.transactions.dto.AccountDto;
import com.example.financial.transactions.dto.AgencyDto;
import com.example.financial.transactions.dto.TransactionCsvDto;
import com.example.financial.transactions.exception.StorageFileNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TransactionController {

    private final StorageService storageService;
    private final JobLauncher jobLauncher;
    private final Job importTransactionJob;

    @Autowired
    public TransactionController(StorageService storageService, JobLauncher jobLauncher, Job importTransactionJob) {
        this.storageService = storageService;
        this.jobLauncher = jobLauncher;
        this.importTransactionJob = importTransactionJob;
    }

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        var file = transactionService.getResourceResponseEntity(filename, storageService);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/transactions")
    @ResponseBody
    public List<TransactionCsvDto> getTransactions() throws IOException {
        // Return a list of transactions with `transaction_date` and `import_date`
        return transactionService.getTransactionsFromFiles(storageService);
    }

    @GetMapping("/transactions/details/{importDate}")
    @ResponseBody
    public List<TransactionCsvDto> detailTransaction(@PathVariable String importDate) {
        // Return a list of transactions with `transaction_date` and `import_date`
        return transactionService.getTransactionsByImportDate(importDate);
    }

    @GetMapping("/transactions/analyses/{year}/{month}")
    @ResponseBody
    public List<TransactionCsvDto> listSuspectTransactions(@PathVariable int year, @PathVariable int month) {
        return transactionService.getSuspectTransactionsByYearAndMonth(year, month);
    }

    @GetMapping("/accounts/analyses/{year}/{month}")
    @ResponseBody
    public List<AccountDto> listSuspectAccounts(@PathVariable int year, @PathVariable int month) {
        return transactionService.getSuspectAccountsByYearAndMonth(year, month);
    }

    @GetMapping("/agencies/analyses/{year}/{month}")
    @ResponseBody
    public List<AgencyDto> listSuspectAgencies(@PathVariable int year, @PathVariable int month) {
       return transactionService.getSuspectAgenciesByYearAndMonth(year, month);
    }

    @PostMapping("/")
    public String handleCsvFileUpload(@RequestParam("file") MultipartFile csvFile, @RequestParam("token") String token, RedirectAttributes redirectAttributes) throws JsonProcessingException {
        transactionService.csvFileUpload(csvFile, token, redirectAttributes, jobLauncher, importTransactionJob, storageService);
        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
