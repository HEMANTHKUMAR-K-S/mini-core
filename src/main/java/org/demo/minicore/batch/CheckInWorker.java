
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
public class CheckInWorker {

    private final Logger log =
            LoggerFactory.getLogger(CheckInWorker.class);

    private final RecordClaimService recordClaimService;
    private final JobOperator jobOperator;
    private final Job checkInJob;

    public CheckInWorker(
            RecordClaimService recordClaimService,
            JobOperator jobOperator,
            @Qualifier("checkInJob") Job checkInJob) {

        this.recordClaimService = recordClaimService;
        this.jobOperator = jobOperator;
        this.checkInJob = checkInJob;
    }


    public void process() {

        while (!Thread.currentThread().isInterrupted()) {

            String txId = recordClaimService.claimRecords("CHECK-IN");

            if (txId == null) {
                log.info("No pending Check-In records available.");
                return;
            }

            log.info("Check-In worker started. txId: {}", txId);

            try {
                JobParameters jobParameters =
                        new JobParametersBuilder()
                                .addString("txId", txId)
                                .toJobParameters();

                JobExecution execution =
                        jobOperator.start(checkInJob, jobParameters);

                log.info(
                        "Check-In Job completed. txId: {}, Status: {}",
                        txId,
                        execution.getStatus()
                );

                if (execution.getStatus().isUnsuccessful()) {
                    log.error("Check-In batch failed. Moving to next batch. txId: {}", txId);
                }

            } catch (Exception e) {
                log.error(
                        "Check-In Job failed. Moving to next batch. txId: {}",
                        txId,
                        e
                );
            }
        }
    }


//    public void process() {
//
//        String txId = recordClaimService.claimRecords("CHECK-IN");
//
//        if (txId == null) {
//            log.info("No pending Check-In records available.");
//            return;
//        }
//
//        log.info("Check-In worker started. txId: {}", txId);
//
//        try {
//            JobParameters jobParameters =
//                    new JobParametersBuilder()
//                            .addString("txId", txId)
//                            .toJobParameters();
//
//            JobExecution execution =
//                    jobOperator.start(checkInJob, jobParameters);
//
//            log.info(
//                    "Check-In Job submitted. txId: {}, Status: {}",
//                    txId,
//                    execution.getStatus()
//            );
//
//        } catch (Exception e) {
//            log.error("Failed to submit Check-In Job. txId: {}", txId, e);
//            throw new RuntimeException(e);
//        }
//    }
}
