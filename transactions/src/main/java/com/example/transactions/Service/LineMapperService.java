package com.example.transactions.Service;

import com.example.transactions.model.LocalDateTimeEditor;
import com.example.transactions.model.TransactionRecord;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.example.transactions.util.SqlFields.listFields;

@Service
public class LineMapperService {


    public LineMapper<TransactionRecord> lineMapper() {
        DefaultLineMapper<TransactionRecord> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(listFields);

        BeanWrapperFieldSetMapper<TransactionRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TransactionRecord.class);

        fieldSetMapper.setCustomEditors(Collections.singletonMap(
                LocalDateTime.class, new LocalDateTimeEditor("yyyy-MM-dd'T'HH:mm:ss")
        ));

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }
}
