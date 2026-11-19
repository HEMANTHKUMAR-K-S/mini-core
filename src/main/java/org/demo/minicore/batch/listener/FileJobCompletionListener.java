
package org.demo.minicore.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.demo.minicore.filter.FileDuplicateFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

@Slf4j
@Component
public class FileJobCompletionListener implements JobExecutionListener {

    private final FileDuplicateFilter duplicateFilter;

//    private static final Logger log=  LoggerFactory.getLogger(FileJobCompletionListener.class);

    public FileJobCompletionListener(FileDuplicateFilter duplicateFilter) {
        this.duplicateFilter = duplicateFilter;
    }


    @Override
    public void afterJob(JobExecution jobExecution) {

        String inputFile = jobExecution.getJobParameters()
                .getString("inputFile");
        if (inputFile == null) {
           log.error("inputFile parameter is missing!");
            return;
        }

        Path filePath = Path.of(inputFile);

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            try {

                long fileSize = jobExecution.getJobParameters()
                        .getLong("fileSize");

                long lastModified = jobExecution.getJobParameters()
                        .getLong("lastModified");

                FileDuplicateFilter.FileSignature signature =
                        new FileDuplicateFilter.FileSignature(
                                fileSize,
                                FileTime.fromMillis(lastModified)
                        );

                duplicateFilter.markProcessed(filePath, signature);

                log.info("Batch job completed successfully.");
                log.info("File marked as processed: " + filePath);

            } catch (Exception e) {

                log.info(
                        "Could not mark file as processed: " + filePath
                );

                e.printStackTrace();
            }

        } else {

            duplicateFilter.release(filePath);

            log.info("Batch job failed or stopped.");
            log.info("File released for retry: " + filePath);
        }
    }

}
