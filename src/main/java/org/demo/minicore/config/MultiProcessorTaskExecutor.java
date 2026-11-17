//
//package org.demo.minicore.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.task.TaskRejectedException;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.concurrent.Executor;
//import java.util.concurrent.RejectedExecutionException;
//
//public class MultiProcessorTaskExecutor implements Executor {
//
//    private static final Logger log =
//            LoggerFactory.getLogger(MultiProcessorTaskExecutor.class);
//
//    private final List<ThreadPoolTaskExecutor> processorPools =
//            new ArrayList<>();
//
//    private int nextTieBreakIndex = 0;
//
//    public MultiProcessorTaskExecutor(
//            int processorCount,
//            int queueCapacity,
//            int maxPoolSize) {
//
//        for (int i = 1; i <= processorCount; i++) {
//
//            ThreadPoolTaskExecutor executor =
//                    new ThreadPoolTaskExecutor();
//
//            executor.setCorePoolSize(1);
//            executor.setMaxPoolSize(maxPoolSize);
//            executor.setQueueCapacity(queueCapacity);
//            executor.setKeepAliveSeconds(60);
//            executor.setThreadNamePrefix(
//                    "CheckIn" + i + "-"
//            );
//
//            executor.setRejectedExecutionHandler(
//                    new java.util.concurrent.ThreadPoolExecutor.AbortPolicy()
//            );
//
//            executor.setWaitForTasksToCompleteOnShutdown(true);
//            executor.setAwaitTerminationSeconds(30);
//
//            executor.initialize();
//
//            processorPools.add(executor);
//
//            log.info(
//                    "Created Processor Pool {} | Queue Capacity: {} | Max Workers: {}",
//                    i, queueCapacity, maxPoolSize
//            );
//        }
//    }
//
//    @Override
//    public synchronized void execute(Runnable task) {
//
//        // Create a list of pools with their current load
//        List<PoolLoad> poolLoads = new ArrayList<>();// initially it is empty
//
//        //responsible to create processor pools
//        for (int i = 0; i < processorPools.size(); i++) {
//
//            ThreadPoolTaskExecutor executor =
//                    processorPools.get(i);
//
//            int activeThreads = executor.getActiveCount();
//
//            int queuedTasks = executor.getThreadPoolExecutor()
//                    .getQueue()
//                    .size();
//
//            int load = activeThreads + queuedTasks;
//
//            poolLoads.add(
//                    new PoolLoad(i, executor, load)
//            );
//        }
//
//        // Prefer the least-loaded pool.
//        // Rotate tie-breaking priority to avoid always favoring Pool 1.
//        int startIndex = nextTieBreakIndex;
//
//        poolLoads.sort(
//                Comparator.comparingInt(PoolLoad::load)
//                        .thenComparingInt(pool ->
//                                (pool.index() - startIndex
//                                        + processorPools.size())
//                                        % processorPools.size())
//        );
//
//
//
//
//
//        // Try pools from least busy to most busy
//        RejectedExecutionException lastException = null;
//
//        for (PoolLoad pool : poolLoads) {
//
//            try {
//
//                pool.executor().execute(task);
//
//                nextTieBreakIndex =
//                        (pool.index() + 1) % processorPools.size();
//
//                log.info(
//                        "Task submitted to Processor Pool {} | Load before submission: {}",
//                        pool.index() + 1,
//                        pool.load()
//                );
//
//                return;
//
//            } catch (RejectedExecutionException exception) {
//
//                lastException = exception;
//
//                log.warn(
//                        "Processor Pool {} rejected task. Trying next least-busy pool.",
//                        pool.index() + 1
//                );
//            }
//        }
//
//        log.error("All processor pools are full. Task rejected.");
//
//        throw new TaskRejectedException(
//                "All MiniCore processor pools are full",
//                lastException
//        );
//    }
//
//    public void shutdown() {
//
//        for (ThreadPoolTaskExecutor executor : processorPools) {
//            executor.shutdown();
//        }
//
//        log.info("All MiniCore processor pools shut down.");
//    }
//
//    private record PoolLoad(
//            int index,
//            ThreadPoolTaskExecutor executor,
//            int load
//    ) {
//    }
//}
