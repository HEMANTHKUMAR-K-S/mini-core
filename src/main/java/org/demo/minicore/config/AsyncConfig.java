
package org.demo.minicore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncConfig {

    @Bean(destroyMethod = "shutdown")
    public MultiProcessorTaskExecutor checkInExecutor() {

        int processorCount =
                Runtime.getRuntime().availableProcessors();

        int queueCapacity = processorCount;

        int maxPoolSize = 100;

        return new MultiProcessorTaskExecutor(
                processorCount,
                queueCapacity,
                maxPoolSize
        );
    }
}
