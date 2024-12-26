package com.example.transactions.controller;

import com.example.transactions.Service.StorageService;
import com.example.transactions.Service.TransactionService;
import com.example.transactions.dto.AccountDto;
import com.example.transactions.dto.AgencyDto;
import com.example.transactions.dto.TransactionDto;
import com.example.transactions.exception.StorageFileNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class TransactionController {

    private final StorageService storageService;
    private final JobLauncher jobLauncher;
    private final Job importTransactionJobCsv;
    private final Job importTransactionJobXml;

    @Autowired
    public TransactionController(StorageService storageService, JobLauncher jobLauncher, @Qualifier("importTransactionJobCsv") Job importTransactionJobCsv,
                                 @Qualifier("importTransactionJobXml") Job importTransactionJobXml) {
        this.storageService = storageService;
        this.jobLauncher = jobLauncher;
        this.importTransactionJobCsv = importTransactionJobCsv;
        this.importTransactionJobXml = importTransactionJobXml;
    }

    @Autowired
    private TransactionService transactionService;

    @GetMapping("files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        var file = transactionService.getResourceResponseEntity(filename, storageService);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("transactions")
    @ResponseBody
    public List<TransactionDto> getTransactions() throws IOException {
        return transactionService.getTransactionsFromFiles(storageService);
    }

    @GetMapping("transactions/details/{importDate}")
    @ResponseBody
    public List<TransactionDto> detailTransaction(@PathVariable String importDate) {
        return transactionService.getTransactionsByImportDate(importDate);
    }

    @GetMapping("transactions/analyses/{year}/{month}")
    @ResponseBody
    public List<TransactionDto> listSuspectTransactions(@PathVariable int year, @PathVariable int month) {
        return transactionService.getSuspectTransactionsByYearAndMonth(year, month);
    }

    @GetMapping("transactions/accounts/analyses/{year}/{month}")
    @ResponseBody
    public List<AccountDto> listSuspectAccounts(@PathVariable int year, @PathVariable int month) {
        return transactionService.getSuspectAccountsByYearAndMonth(year, month);
    }

    @GetMapping("transactions/agencies/analyses/{year}/{month}")
    @ResponseBody
    public List<AgencyDto> listSuspectAgencies(@PathVariable int year, @PathVariable int month) {
       return transactionService.getSuspectAgenciesByYearAndMonth(year, month);
    }

    @PostMapping("transactions")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("token") String token, RedirectAttributes redirectAttributes) throws JsonProcessingException {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());

        if ("csv".equalsIgnoreCase(fileExtension)) {
            transactionService.csvFileUpload(file, token, redirectAttributes, jobLauncher, importTransactionJobCsv, storageService);
        } else if ("xml".equalsIgnoreCase(fileExtension)) {
            transactionService.xmlFileUpload(file, token, redirectAttributes, jobLauncher, importTransactionJobXml, storageService);
        } else {
            redirectAttributes.addFlashAttribute("message", "Unsupported file type: " + fileExtension);
            return "redirect:http://127.0.0.1:5500/html/import.html";
        }
        return "redirect:http://127.0.0.1:5500/html/import.html";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
