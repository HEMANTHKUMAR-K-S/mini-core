package org.demo.minicore.batch;

import org.demo.minicore.entity.UserLogs;
import org.demo.minicore.repository.UserLogRepo;
import org.demo.minicore.service.CheckInProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.List;

@Component
public class CheckOutTasklet implements Tasklet {

    private final Logger log =
            LoggerFactory.getLogger(CheckOutTasklet.class);


    private final RetryTemplate retryTemplate;
    private final CheckInProcessService checkInProcessService;

    public CheckOutTasklet(RetryTemplate retryTemplate, CheckInProcessService checkInProcessService) {
        this.retryTemplate = retryTemplate;
        this.checkInProcessService = checkInProcessService;
    }


    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext) {

        String txId = (String) chunkContext
                .getStepContext()
                .getJobParameters()
                .get("txId");


        try {
            retryTemplate.execute(context->{

                int attempt = context.getRetryCount() + 1;

//                log.info(
//                        "Check-In processing attempt: {} for txId: {}",
//                        attempt,
//                        txId
//                );

                checkInProcessService.process(txId);

                return null;
            });
        } catch (SocketTimeoutException e) {
            throw new RuntimeException(e);
        }
//        List<UserLogs> records =
//                userLogRepo.findAllByTxId(txId);

//        log.info("Processing {} Check-Out records for txId: {}",
//                records.size(), txId);

//        records.forEach(record ->
//                log.info("Processing Check-Out record: {}", record));

        return RepeatStatus.FINISHED;
    }
}