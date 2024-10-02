package com.example.financial.transactions.controller;

import com.example.financial.transactions.Service.StorageService;
import com.example.financial.transactions.exception.StorageFileNotFoundException;
import com.example.financial.transactions.repository.TransactionRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

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
    private TransactionRepository repository;


    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FinancialTransactionController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleCsvFileUpload(@RequestParam("file") MultipartFile csvFile,
                                   RedirectAttributes redirectAttributes) {

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

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
