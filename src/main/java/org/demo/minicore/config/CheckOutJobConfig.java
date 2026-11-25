
package org.demo.minicore.config;

import org.demo.minicore.batch.CheckOutJobListener;
import org.demo.minicore.batch.CheckOutTasklet;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CheckOutJobConfig {

    @Bean
    public Step checkOutStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            CheckOutTasklet checkOutTasklet) {

        return new StepBuilder("checkOutStep", jobRepository)
                .tasklet(checkOutTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job checkOutJob(
            JobRepository jobRepository,
            Step checkOutStep,
            CheckOutJobListener checkOutJobListener) {

        return new JobBuilder("checkOutJob", jobRepository)
                .start(checkOutStep)
                .listener(checkOutJobListener)
                .build();
    }
}
