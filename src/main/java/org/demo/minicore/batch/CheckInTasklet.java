package org.demo.minicore.batch;

import org.demo.minicore.entity.UserLogs;
import org.demo.minicore.repository.UserLogRepo;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckInTasklet implements Tasklet {

    Logger log= LoggerFactory.getLogger(CheckInTasklet.class);

    private final UserLogRepo userLogRepo;

    public CheckInTasklet(UserLogRepo userLogRepo) {
        this.userLogRepo = userLogRepo;
    }


    @Override
    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        String txId= (String) chunkContext.getStepContext()
                .getJobParameters()
                .get("txId");

        List<UserLogs> records = userLogRepo.findAllByTxId(txId);

        log.info("Records fetched for txId {}: {}", txId, records.size());

//        for (UserLogs record : records) {
//
//            log.info(
//                    "Processing Check-In record. ID: {}, Username: {}",
//                    record.getId(),
//                    record.getUsername()
//            );
//        }

//        log.info("Check-In processing completed. txId: {}", txId);
        return RepeatStatus.FINISHED;
    }
}
