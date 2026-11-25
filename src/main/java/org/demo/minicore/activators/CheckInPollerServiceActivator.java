package org.demo.minicore.activators;


import org.demo.minicore.batch.CheckInWorkerExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckInPollerServiceActivator {

    private final Logger log= LoggerFactory.getLogger(CheckInPollerServiceActivator.class);

    private final CheckInWorkerExecutor checkInWorkerExecutor;

    public CheckInPollerServiceActivator(CheckInWorkerExecutor checkInWorkerExecutor) {
        this.checkInWorkerExecutor = checkInWorkerExecutor;
    }

    @ServiceActivator(inputChannel = "checkInCountChannel")
    public void checkInCount(List<Long> results){
        long count =results.isEmpty()? 0:results.get(0);


        log.info("Total check-ins : "+count);


        if (count>0){

            checkInWorkerExecutor.submit();
        }
    }

}
