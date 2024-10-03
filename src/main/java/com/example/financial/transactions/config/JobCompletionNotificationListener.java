package com.example.financial.transactions.config;

import com.example.financial.transactions.dto.TransactionDto;
import com.example.financial.transactions.model.TransactionCsvRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            jdbcTemplate
                    .query("SELECT original_bank, original_agency, original_account, destiny_bank, destiny_agency, destiny_account, amount, transaction_time FROM transactions", new DataClassRowMapper<>(TransactionCsvRecord.class))
                    .forEach(transaction -> log.info("Found <{{}}> in the database.", transaction));
        }
    }
}
