
package org.demo.minicore.rmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DatabaseThresholdMonitor {

    private static final long THRESHOLD = 200;

    private final ListenerControllerService listenerControllerService;

    private long checkInCount = -1;
    private long checkOutCount = -1;

    private boolean listenerStopped = false;

    public DatabaseThresholdMonitor(
            ListenerControllerService listenerControllerService) {
        this.listenerControllerService = listenerControllerService;
    }

    public synchronized void updateCheckInCount(long count) {
        this.checkInCount = count;
        checkThreshold();
    }

    public synchronized void updateCheckOutCount(long count) {
        this.checkOutCount = count;
        checkThreshold();
    }


    private void checkThreshold() {

        // Wait until both pollers provide their first count.
        if (checkInCount < 0 || checkOutCount < 0) {
            return;
        }

        long totalRecords = checkInCount + checkOutCount;

        log.info(
                "Check-In: {}, Check-Out: {}, Combined records: {}",
                checkInCount, checkOutCount, totalRecords
        );

        if (totalRecords >= THRESHOLD && !listenerStopped) {

            listenerControllerService.stopListener();
            listenerStopped = true;

        } else if (totalRecords < THRESHOLD && listenerStopped) {

            listenerControllerService.resumeListener();
            listenerStopped = false;
        }
    }
}
