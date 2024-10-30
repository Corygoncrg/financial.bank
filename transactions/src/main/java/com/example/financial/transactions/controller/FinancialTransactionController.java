package com.example.financial.transactions.controller;

import com.example.financial.transactions.Service.StorageService;
import com.example.financial.transactions.Service.TransactionService;
import com.example.financial.transactions.exception.StorageFileNotFoundException;
import com.example.financial.transactions.model.TransactionCsvRecord;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class FinancialTransactionController {

    private final StorageService storageService;
    private final JobLauncher jobLauncher;
    private final Job importTransactionJob;

    @Autowired
    public FinancialTransactionController(StorageService storageService, JobLauncher jobLauncher, Job importTransactionJob) {
        this.storageService = storageService;
        this.jobLauncher = jobLauncher;
        this.importTransactionJob = importTransactionJob;
    }

    @Autowired
    private TransactionService transactionService;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }

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
    public List<TransactionCsvRecord> getTransactions() throws IOException {
        // Return a list of transactions with `transaction_date` and `import_date`
        return transactionService.getTransactionsFromFiles(storageService);
    }

    @PostMapping("/")
    public String handleCsvFileUpload(@RequestParam("file") MultipartFile csvFile, @RequestParam("userId") String userId, RedirectAttributes redirectAttributes) {
        transactionService.csvFileUpload(csvFile, userId, redirectAttributes, jobLauncher, importTransactionJob, storageService);
        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
