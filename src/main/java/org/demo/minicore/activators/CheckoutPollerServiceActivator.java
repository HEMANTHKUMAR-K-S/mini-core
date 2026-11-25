package org.demo.minicore.activators;


//import org.demo.minicore.batch.CheckOutWorkerExecutor;
import org.demo.minicore.batch.CheckOutWorkerExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutPollerServiceActivator {

private final CheckOutWorkerExecutor workerExecutor;

    private final Logger log= LoggerFactory.getLogger(CheckoutPollerServiceActivator.class);

    public CheckoutPollerServiceActivator(CheckOutWorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor;
    }

    @ServiceActivator(inputChannel = "checkOutCountChannel")
    public void CheckoutCount(List<Long> results){

        long count = results.isEmpty()? 0: results.get(0);

        log.info("Total check-outs : " + count);

        if (count > 0) {
            workerExecutor.submitWorker();
        }
    }

}
