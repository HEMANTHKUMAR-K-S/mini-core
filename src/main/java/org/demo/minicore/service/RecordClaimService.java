package org.demo.minicore.service;

import org.demo.minicore.entity.UserLogs;
import org.demo.minicore.properties.JobProperties;
import org.demo.minicore.repository.UserLogRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RecordClaimService {

    private final UserLogRepo userLogsRepository;
    private final JobProperties jobProperties;

    public RecordClaimService(
            UserLogRepo userLogsRepository,
            JobProperties jobProperties) {
        this.userLogsRepository = userLogsRepository;
        this.jobProperties = jobProperties;
    }

    @Transactional
    public String claimRecords(String status) {

        int batchSize = jobProperties.getJobSize();

        if (batchSize <= 0) {
            throw new IllegalStateException(
                    "Batch fetch size must be greater than zero. Configured size: " + batchSize
            );
        }

        Pageable pageable = PageRequest.of(0, batchSize);

        List<UserLogs> records =
                userLogsRepository.findPendingRecords(status, pageable);

        if (records.isEmpty()) {
            return null;
        }

        String txId = UUID.randomUUID().toString();

        records.forEach(record -> record.setTxId(txId));

        userLogsRepository.saveAll(records);
        return txId;
    }
}