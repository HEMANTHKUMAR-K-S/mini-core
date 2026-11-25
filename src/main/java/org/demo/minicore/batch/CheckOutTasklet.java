package org.demo.minicore.batch;

import org.demo.minicore.entity.UserLogs;
import org.demo.minicore.repository.UserLogRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckOutTasklet implements Tasklet {

    private final Logger log =
            LoggerFactory.getLogger(CheckOutTasklet.class);

    private final UserLogRepo userLogRepo;

    public CheckOutTasklet(UserLogRepo userLogRepo) {
        this.userLogRepo = userLogRepo;
    }

    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext) {

        String txId = (String) chunkContext
                .getStepContext()
                .getJobParameters()
                .get("txId");

        List<UserLogs> records =
                userLogRepo.findAllByTxId(txId);

//        log.info("Processing {} Check-Out records for txId: {}",
//                records.size(), txId);

//        records.forEach(record ->
//                log.info("Processing Check-Out record: {}", record));

        return RepeatStatus.FINISHED;
    }
}