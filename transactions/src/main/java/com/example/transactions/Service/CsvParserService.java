package com.example.transactions.Service;

import com.example.transactions.model.LocalDateTimeEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParserService {

    private final LocalDateTimeEditor localDateTimeEditor;

    @Autowired
    public CsvParserService() {
        this.localDateTimeEditor = new LocalDateTimeEditor("yyyy-MM-dd'T'HH:mm:ss");  // Customize the pattern based on your CSV data format
    }

    public List<LocalDateTime> extractTransactionDatesFromFile(List<String> fileContent) {
        List<LocalDateTime> transactionDates = new ArrayList<>();

        for (String line : fileContent) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] columns = line.split(",");

            if (columns.length < 8) {
                System.err.println("Skipping malformed line: " + line);
                continue;
            }

            try {
                localDateTimeEditor.setAsText(columns[7]);
                LocalDateTime transactionDate = (LocalDateTime) localDateTimeEditor.getValue();

                transactionDates.add(transactionDate);

            } catch (Exception e) {
                System.err.println("Error parsing line: " + line + ". Skipping...");
                e.printStackTrace();
            }
        }

        return transactionDates;
    }
}
