package com.example.financial.transactions.Service;

import com.example.financial.transactions.model.LocalDateTimeEditor;
import com.example.financial.transactions.model.TransactionCsvRecord;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.example.financial.transactions.Service.SqlService.listFields;

@Service
public class LineMapperService {


    public LineMapper<TransactionCsvRecord> lineMapper() {
        DefaultLineMapper<TransactionCsvRecord> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(listFields);

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
