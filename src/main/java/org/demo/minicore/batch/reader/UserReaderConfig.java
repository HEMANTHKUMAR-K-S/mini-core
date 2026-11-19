
package org.demo.minicore.batch.reader;

import org.demo.minicore.batch.model.CsvRecords;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class UserReaderConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<CsvRecords> userReader(
            @Value("#{jobParameters['inputFile']}") String inputFile) {

        return new FlatFileItemReaderBuilder<CsvRecords>()
                .name("userReader")
                .resource(new FileSystemResource(inputFile))
                .delimited()
                .names("id", "name", "age", "location", "salary")
                .targetType(CsvRecords.class)
                .linesToSkip(1)
                .build();
    }
}
