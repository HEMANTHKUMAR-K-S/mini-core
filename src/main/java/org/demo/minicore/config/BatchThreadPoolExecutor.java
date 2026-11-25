package org.demo.minicore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class BatchThreadPoolExecutor {


    private int processSize=Runtime.getRuntime().availableProcessors();

    @Bean("checkInThreadPool")
    public ThreadPoolTaskExecutor checkInThreadPool(){

        ThreadPoolTaskExecutor executor =new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(processSize);
        executor.setMaxPoolSize(processSize);
        executor.setQueueCapacity(processSize);
        executor.setKeepAliveSeconds(30);
        executor.setThreadNamePrefix("CheckInPool-");

        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.AbortPolicy()
        );

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }

    @Bean("checkOutThreadPool")
    public ThreadPoolTaskExecutor checkOutThreadPool(){

        ThreadPoolTaskExecutor executor =new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(processSize);
        executor.setMaxPoolSize(processSize);
        executor.setQueueCapacity(processSize);
        executor.setKeepAliveSeconds(30);
        executor.setThreadNamePrefix("CheckOutPool-");

        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.AbortPolicy()
        );

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }

}
