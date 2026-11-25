
package org.demo.minicore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadExecutorConfig {


    private int processSize=Runtime.getRuntime().availableProcessors();


    @Bean(name = "checkInExecutor")
    public ThreadPoolTaskExecutor checkInExecutor(){

        ThreadPoolTaskExecutor executor =new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(processSize);
        executor.setQueueCapacity(processSize);
        executor.setKeepAliveSeconds(30);
        executor.setThreadNamePrefix("CheckIn-");

        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.AbortPolicy()
        );

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }

    @Bean(name = "checkOutExecutor")
    public ThreadPoolTaskExecutor checkOutExecutor(){

        ThreadPoolTaskExecutor executor =new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(processSize);
        executor.setQueueCapacity(processSize);
        executor.setKeepAliveSeconds(30);
        executor.setThreadNamePrefix("CheckOut-");

        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.AbortPolicy()
        );

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }
}
