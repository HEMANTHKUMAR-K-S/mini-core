package org.demo.minicore.batch;

import org.demo.minicore.repository.UserLogRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class CheckInJobListener implements JobExecutionListener {

    private final UserLogRepo userLogRepo;

    private Logger log =  LoggerFactory.getLogger(CheckInJobListener.class);

    public CheckInJobListener(UserLogRepo userLogRepo) {
        this.userLogRepo = userLogRepo;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {


        String txId = jobExecution.getJobParameters().getString("txId");


        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            int deletedCount = userLogRepo.deleteAllByTxId(txId);

            log.info(
                    "Check-In Job completed. Deleted {} records for txId: {}",
                    deletedCount,
                    txId
            );

        }
        else {
            log.error(
                    "Check-In Job failed. Records retained for txId: {}. Status: {}",
                    txId,
                    jobExecution.getStatus()
            );
        }
    }
}
