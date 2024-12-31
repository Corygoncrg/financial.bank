package com.example.transactions.config;

import com.example.transactions.Service.TransactionBatchService;
import com.example.transactions.dto.TransactionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import static com.example.transactions.util.SqlFields.allFields;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionBatchService batchService;

    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results{}", jobExecution.getCreateTime());

            jdbcTemplate
                    .query("SELECT " + allFields + " FROM transactions", new DataClassRowMapper<>(TransactionDto.class))
                    .forEach(transaction -> log.info("Found <{{}}> in the database.", transaction));
            
            batchService.resetState();
        }
    }

}
