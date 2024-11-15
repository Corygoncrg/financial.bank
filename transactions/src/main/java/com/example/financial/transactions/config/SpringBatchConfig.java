package com.example.financial.transactions.config;

import com.example.financial.transactions.Service.TransactionBatchService;
import com.example.financial.transactions.Service.LineMapperService;
import com.example.financial.transactions.dto.UserDto;
import com.example.financial.transactions.model.Transaction;
import com.example.financial.transactions.model.TransactionRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

import java.time.LocalDateTime;

import static com.example.financial.transactions.Service.SqlService.*;
import static com.example.financial.transactions.config.StorageProperties.uploadDirLocation;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    @Autowired
    private LineMapperService lineMapperService;

    @Autowired
    private TransactionBatchService batchService;

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @StepScope
    public FlatFileItemReader<TransactionRecord> reader(@Value("#{jobParameters[filename]}") String filename) {
        return new FlatFileItemReaderBuilder<TransactionRecord>()
                .name("transactionItemReader")
                .resource(new FileSystemResource( uploadDirLocation + "/" + filename))
                .delimited()
                .names(listFields)
                .targetType(TransactionRecord.class)
                .lineMapper(lineMapperService.lineMapper())
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<TransactionRecord, Transaction> processor(@Value("#{stepExecution.jobExecution.createTime}") LocalDateTime importDate,
                                                                   @Value("#{jobParameters['userDto']}") String userDtoJson) throws JsonProcessingException {
        UserDto dto = new ObjectMapper().readValue(userDtoJson, UserDto.class);
        return record -> batchService.processRecord(record, importDate, dto);
    }

    @Bean
    public JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .sql("INSERT INTO transactions (" + allFields + ") VALUES (" + allJavaFields +")")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Job importTransactionJob(JobRepository repository, Step stepCsv, JobCompletionNotificationListener listener) {
        return new JobBuilder("importTransactionJob", repository)
                .listener(listener)
                .start(stepCsv)
                .build();
    }

    @Bean
    public Step stepCsv(JobRepository jobRepository,
                      DataSourceTransactionManager transactionManager,
                      FlatFileItemReader<TransactionRecord> reader,
                      JdbcBatchItemWriter<Transaction> writer,
                      ItemProcessor<TransactionRecord, Transaction> processor) {
        return new StepBuilder("stepCsv", jobRepository)
                .<TransactionRecord, Transaction>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemReader<TransactionRecord> xmlReader(@Value("#{jobParameters['filename']}") String filename) {
        StaxEventItemReader<TransactionRecord> reader = new StaxEventItemReader<>();
        reader.setResource(new FileSystemResource(uploadDirLocation + "/" + filename));
        reader.setFragmentRootElementName("transacao");  // Root element of each record in the XML
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(TransactionRecord.class);
        reader.setUnmarshaller(unmarshaller);
        return reader;
    }

    @Bean
    public Job importTransactionJobXml(JobRepository repository, Step stepXml, JobCompletionNotificationListener listener) {
        return new JobBuilder("importTransactionJobXml", repository)
                .listener(listener)
                .start(stepXml)
                .build();
    }

    @Bean
    public Step stepXml(JobRepository jobRepository,
                        DataSourceTransactionManager transactionManager,
                        StaxEventItemReader<TransactionRecord> xmlReader,
                        JdbcBatchItemWriter<Transaction> writer,
                        ItemProcessor<TransactionRecord, Transaction> processor) {
        return new StepBuilder("stepXml", jobRepository)
                .<TransactionRecord, Transaction>chunk(3, transactionManager)
                .reader(xmlReader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
