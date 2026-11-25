package org.demo.minicore.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class CheckInWorkerExecutor {

    private static final Logger log =
            LoggerFactory.getLogger(CheckInWorkerExecutor.class);

    private final CheckInWorker checkInWorker;
    private final TaskExecutor checkInThreadPool;

    public CheckInWorkerExecutor(
            CheckInWorker checkInWorker,
            @Qualifier("checkInThreadPool")
            TaskExecutor checkInThreadPool) {

        this.checkInWorker = checkInWorker;
        this.checkInThreadPool = checkInThreadPool;
    }

    public void submit() {

        checkInThreadPool.execute(() -> {

            try {
                log.info(
                        "Submitting Check-In worker on thread: {}",
                        Thread.currentThread().getName()
                );

                checkInWorker.process();

            } catch (Exception e) {

                log.error(
                        "Check-In worker execution failed",
                        e
                );
            }
        });
    }
}