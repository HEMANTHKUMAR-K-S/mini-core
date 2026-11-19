
package org.demo.minicore.batch.config;

import org.demo.minicore.batch.listener.FileJobCompletionListener;
import org.demo.minicore.batch.model.CsvRecords;
import org.demo.minicore.batch.processor.UserProcessor;
import org.demo.minicore.batch.tasklet.FileBackUpTasklet;
import org.demo.minicore.batch.writer.UserWriter;
import org.demo.minicore.entity.User;
import org.demo.minicore.service.FileProcessingService;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    // STEP 1: Read CSV and insert users into database
    @Bean
    public Step userStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<CsvRecords> userReader,
            UserProcessor userProcessor,
            UserWriter userWriter) {

        return new StepBuilder("userStep", jobRepository)
                .<CsvRecords, User>chunk(5)
                .reader(userReader)
                .processor(userProcessor)
                .writer(userWriter)
                .transactionManager(transactionManager)
                .build();
    }

    // Create backup tasklet
    @Bean
    @StepScope
    public FileBackUpTasklet fileBackUpTasklet(
            FileProcessingService fileProcessingService,
            @Value("#{jobParameters['inputFile']}") String inputFile) {

        return new FileBackUpTasklet(
                fileProcessingService,
                inputFile
        );
    }

    // STEP 2: Back up the original CSV
    @Bean
    public Step backupStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FileBackUpTasklet fileBackUpTasklet) {

        return new StepBuilder("backupStep", jobRepository)
                .tasklet(fileBackUpTasklet, transactionManager)
                .build();
    }

    // Execute both steps in sequence
    @Bean
    public Job userJob(
            JobRepository jobRepository,
            Step userStep,
            Step backupStep,
            FileJobCompletionListener listener) {

        return new JobBuilder("userJob", jobRepository)
                .start(userStep)
                .next(backupStep)
                .listener(listener)
                .build();
    }
}
