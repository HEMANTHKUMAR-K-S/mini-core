package org.demo.minicore.activators;


import org.demo.minicore.batch.CheckInWorkerExecutor;
import org.demo.minicore.rmq.DatabaseThresholdMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckInPollerServiceActivator {

    private final Logger log= LoggerFactory.getLogger(CheckInPollerServiceActivator.class);

    private final CheckInWorkerExecutor checkInWorkerExecutor;
    private final DatabaseThresholdMonitor databaseThresholdMonitor;

    public CheckInPollerServiceActivator(CheckInWorkerExecutor checkInWorkerExecutor, DatabaseThresholdMonitor databaseThresholdMonitor) {
        this.checkInWorkerExecutor = checkInWorkerExecutor;
        this.databaseThresholdMonitor = databaseThresholdMonitor;
    }

    @ServiceActivator(inputChannel = "checkInCountChannel")
    public void checkInCount(List<Long> results){
        long count =results.isEmpty()? 0:results.get(0);


        log.info("Total check-ins : "+count);

        databaseThresholdMonitor.updateCheckInCount(count);


        if (count>0){
            checkInWorkerExecutor.submit();
        }
    }

}
