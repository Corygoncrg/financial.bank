package com.example.financial.transactions.config;

import com.example.financial.transactions.Service.LocalDateTimeEditor;
import com.example.financial.transactions.dto.TransactionDto;
import com.example.financial.transactions.model.TransactionCsv;
import com.example.financial.transactions.model.TransactionCsvRecord;
import com.example.financial.transactions.repository.TransactionRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Collections;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {


    @Autowired
    private TransactionRepository repository;

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


    @Bean
    @StepScope
    public FlatFileItemReader<TransactionCsvRecord> reader(@Value("#{jobParameters[filename]}") String filename) {
        return new FlatFileItemReaderBuilder<TransactionCsvRecord>()
                .name("transactionItemReader")
                .resource(new FileSystemResource("upload-dir/" + filename))
                .delimited()
                .names("originalBank", "originalAgency", "originalAccount", "destinyBank", "destinyAgency", "destinyAccount", "amount", "transactionTime")
                .targetType(TransactionCsvRecord.class)
                .lineMapper(lineMapper())
                .build();
    }

    @Bean
    public ItemProcessor<TransactionCsvRecord, TransactionCsv> processor() {
        return csvRecord -> {
            TransactionCsv transaction = new TransactionCsv();
            transaction.setOriginalBank(csvRecord.getOriginalBank());
            transaction.setOriginalAgency(csvRecord.getOriginalAgency());
            transaction.setOriginalAccount(csvRecord.getOriginalAccount());
            transaction.setDestinyBank(csvRecord.getDestinyBank());
            transaction.setDestinyAgency(csvRecord.getDestinyAgency());
            transaction.setDestinyAccount(csvRecord.getDestinyAccount());
            transaction.setAmount(csvRecord.getAmount());
            transaction.setTransactionTime(csvRecord.getTransactionTime());
            return transaction;
        };
    }


    @Bean
    public JdbcBatchItemWriter<TransactionCsv> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<TransactionCsv>()
                .sql("INSERT INTO transactions (originalBank, originalAgency, originalAccount, destinyBank, destinyAgency, destinyAccount, amount, transactionTime) " +
                        "VALUES (:originalBank, :originalAgency, :originalAccount, :destinyBank, :destinyAgency, :destinyAccount, :amount, :transactionTime)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Job importTransactionJob(JobRepository repository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("importTransactionJob", repository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      FlatFileItemReader<TransactionCsvRecord> reader, JdbcBatchItemWriter<TransactionCsv> writer,
                      ItemProcessor<TransactionCsvRecord, TransactionCsv> processor) {
        return new StepBuilder("step1", jobRepository)
                .<TransactionCsvRecord, TransactionCsv>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    private LineMapper<TransactionCsvRecord> lineMapper() {
        DefaultLineMapper<TransactionCsvRecord> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("originalBank","originalAgency","originalAccount",
                "destinyBank","destinyAgency", "destinyAccount", "amount","transactionTime");

        BeanWrapperFieldSetMapper<TransactionCsvRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TransactionCsvRecord.class);

        fieldSetMapper.setCustomEditors(Collections.singletonMap(
                LocalDateTime.class, new LocalDateTimeEditor("yyyy-MM-dd'T'HH:mm:ss")
        ));

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }
}
