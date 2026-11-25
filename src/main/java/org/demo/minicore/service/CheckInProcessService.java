package org.demo.minicore.service;


import org.demo.minicore.entity.UserLogs;
import org.demo.minicore.repository.UserLogRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.SocketTimeoutException;
import java.util.List;

@Service
public class CheckInProcessService {

    private final UserLogRepo userLogRepo;


    Logger log= LoggerFactory.getLogger(CheckInProcessService.class);

    public CheckInProcessService(UserLogRepo userLogRepo) {
        this.userLogRepo = userLogRepo;
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            readOnly = true
    )
    public void process(String txId) throws SocketTimeoutException {


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


//        throw new SocketTimeoutException(
//                "Simulated socket timeout for testing"
//        );
    }
}
