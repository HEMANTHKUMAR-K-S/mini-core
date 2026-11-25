
package org.demo.minicore.batch;

import org.demo.minicore.service.RecordClaimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CheckOutWorker {

    private final Logger log =
            LoggerFactory.getLogger(CheckOutWorker.class);

    private final RecordClaimService recordClaimService;
    private final JobOperator jobOperator;
    private final Job checkOutJob;

    public CheckOutWorker(
            RecordClaimService recordClaimService, JobOperator jobOperator,
            @Qualifier("checkOutJob") Job checkOutJob) {

        this.recordClaimService = recordClaimService;
        this.jobOperator = jobOperator;
        this.checkOutJob = checkOutJob;
    }

    public void process() {

        String txId =
                recordClaimService.claimRecords("CHECK-OUT");

        if (txId == null) {
            log.info("No pending Check-Out records available.");
            return;
        }

//        log.info("Check-Out worker claimed records. txId: {}", txId);

        JobParameters jobParameters =
                new JobParametersBuilder()
                        .addString("txId", txId)
                        .toJobParameters();

        try {
            JobExecution execution =
                    jobOperator.start(checkOutJob, jobParameters);

//            log.info(
//                    "Check-Out job submitted asynchronously. txId: {}, Execution ID: {}",
//                    txId, execution.getId());

        } catch (Exception e) {
            log.error(
                    "Failed to submit Check-Out job. txId: {}",
                    txId, e);
        }
    }
}
