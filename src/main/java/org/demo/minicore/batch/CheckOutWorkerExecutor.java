
package org.demo.minicore.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class CheckOutWorkerExecutor {

    private final Logger log =
            LoggerFactory.getLogger(CheckOutWorkerExecutor.class);

    private final TaskExecutor checkOutThreadPool;
    private final CheckOutWorker checkOutWorker;

    public CheckOutWorkerExecutor(
            @Qualifier("checkOutThreadPool") TaskExecutor checkOutThreadPool,
            CheckOutWorker checkOutWorker) {

        this.checkOutThreadPool = checkOutThreadPool;
        this.checkOutWorker = checkOutWorker;
    }

    public void submitWorker() {

        log.info("Submitting Check-Out worker to thread pool.");

        checkOutThreadPool.execute(() -> {
            try {
                checkOutWorker.process();
            } catch (Exception e) {
                log.error("Error while executing Check-Out worker.", e);
            }
        });
    }
}
