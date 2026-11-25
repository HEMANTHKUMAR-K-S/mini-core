package org.demo.minicore.config;


import org.demo.minicore.batch.CheckInJobListener;
import org.demo.minicore.batch.CheckInTasklet;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CheckInJobConfig {

    @Bean
    public Step checkInStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            CheckInTasklet checkInTasklet
    ) {
        return new StepBuilder("checkInStep", jobRepository)
                .tasklet(checkInTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job checkInJob(JobRepository jobRepository, Step checkInStep, CheckInJobListener checkInJobListener) {
        return new JobBuilder("checkInJob", jobRepository)
                .start(checkInStep)
                .listener(checkInJobListener)
                .build();
    }

}
