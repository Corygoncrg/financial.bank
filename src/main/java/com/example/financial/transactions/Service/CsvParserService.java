package com.example.financial.transactions.Service;

import com.example.financial.transactions.model.LocalDateTimeEditor;
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
            // Skip empty lines
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] columns = line.split(",");

            // Ensure that the line has enough columns before processing
            if (columns.length < 8) {  // Assuming you expect 8 columns, adjust this number based on your CSV structure
                System.err.println("Skipping malformed line: " + line);  // Log the malformed line and skip
                continue;
            }

            try {
                // Parse the date from the expected column (e.g., first column)
                localDateTimeEditor.setAsText(columns[7]);  // Adjust the column index if needed
                LocalDateTime transactionDate = (LocalDateTime) localDateTimeEditor.getValue();

                // Add the parsed date to the list
                transactionDates.add(transactionDate);

            } catch (Exception e) {
                System.err.println("Error parsing line: " + line + ". Skipping...");  // Log the error
                e.printStackTrace();  // Optional: Log the stack trace for debugging
            }
        }

        return transactionDates;
    }
}
