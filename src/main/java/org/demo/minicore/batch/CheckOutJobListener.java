
package org.demo.minicore.batch;

import org.demo.minicore.repository.UserLogRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class CheckOutJobListener implements JobExecutionListener {

    private final Logger log =
            LoggerFactory.getLogger(CheckOutJobListener.class);

    private final UserLogRepo userLogRepo;

    public CheckOutJobListener(UserLogRepo userLogRepo) {
        this.userLogRepo = userLogRepo;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        String txId = (String) jobExecution
                .getJobParameters()
                .getString("txId");

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            int deletedCount =
                    userLogRepo.deleteAllByTxId(txId);

            log.info(
                    "Check-Out Job completed. txId: {}, Deleted records: {}",
                    txId, deletedCount);

        } else {
            log.warn(
                    "Check-Out Job failed. txId: {}. Records retained for retry.",
                    txId);
        }
    }
}
