//
//package org.demo.minicore.config;
//
//import org.springframework.batch.core.configuration.JobRegistry;
//import org.springframework.batch.core.launch.support.TaskExecutorJobOperator;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.task.TaskExecutor;
//
//@Configuration
//public class AsyncJobLauncherConfig {
//
//    @Bean("checkInJobOperator")
//    public TaskExecutorJobOperator checkInJobOperator(
//            JobRepository jobRepository,
//            JobRegistry jobRegistry,
//            @Qualifier("checkInThreadPool") TaskExecutor taskExecutor) {
//
//        TaskExecutorJobOperator operator =
//                new TaskExecutorJobOperator();
//
//        operator.setJobRepository(jobRepository);
//        operator.setJobRegistry(jobRegistry);
//        operator.setTaskExecutor(taskExecutor);
//
//        return operator;
//    }
//
////    @Bean("checkOutJobOperator")
////    public TaskExecutorJobOperator checkOutJobOperator(
////            JobRepository jobRepository,
////            JobRegistry jobRegistry,
////            @Qualifier("checkOutThreadPool") TaskExecutor taskExecutor) {
////
////        TaskExecutorJobOperator operator =
////                new TaskExecutorJobOperator();
////
////        operator.setJobRepository(jobRepository);
////        operator.setJobRegistry(jobRegistry);
////        operator.setTaskExecutor(taskExecutor);
////
////        return operator;
////    }
//}
