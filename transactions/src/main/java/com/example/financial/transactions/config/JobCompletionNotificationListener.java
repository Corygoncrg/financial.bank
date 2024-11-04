package com.example.financial.transactions.config;

import com.example.financial.transactions.model.TransactionCsv;
import com.example.financial.transactions.model.TransactionCsvRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.cglib.core.Local;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.example.financial.transactions.Service.SqlService.allFields;

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
            log.info("!!! JOB FINISHED! Time to verify the results" + jobExecution.getCreateTime());

            jdbcTemplate
                    .query("SELECT " + allFields + " FROM transactions", new DataClassRowMapper<>(TransactionCsvRecord.class))
                    .forEach(transaction -> log.info("Found <{{}}> in the database.", transaction));
        }
    }

}
