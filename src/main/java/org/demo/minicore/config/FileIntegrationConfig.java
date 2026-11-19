
package org.demo.minicore.config;

import lombok.extern.slf4j.Slf4j;
import org.demo.minicore.batch.service.JobLauncherService;
import org.demo.minicore.filter.FileDuplicateFilter;
import org.demo.minicore.service.FileProcessingService;
import org.demo.minicore.watcher.FileSystemEvent;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@Slf4j
@Configuration
@EnableIntegration
public class FileIntegrationConfig {

    @Bean
    public MessageChannel fileEventChannel() {
        return new DirectChannel();
    }

    @Bean
    public FileDuplicateFilter fileDuplicateFilter() {
        return new FileDuplicateFilter();
    }

    @Bean
    public IntegrationFlow fileEventFlow(
            @Qualifier("fileEventChannel") MessageChannel channel,
            FileProcessingService fileProcessingService,
            JobLauncherService jobLauncherService,
            FileDuplicateFilter fileDuplicateFilter) {

        return IntegrationFlow
                .from(channel)
                .handle(FileSystemEvent.class, (event, headers) -> {

                    Path filePath = event.filePath()
                            .toAbsolutePath()
                            .normalize();

                    // Accept CREATE and MODIFY events
                    if (event.eventkind() != ENTRY_CREATE &&
                            event.eventkind() != ENTRY_MODIFY) {
                        return null;
                    }

                    // Accept only CSV files
                    if (!isAllowedFile(filePath)) {
                        log.info(
                                "Unsupported file ignored: " + filePath
                        );
                        return null;
                    }

                    // Ignore directories
                    if (!Files.isRegularFile(filePath)) {
                        return null;
                    }

                    // Duplicate filtering
                    try {

                        if (!fileDuplicateFilter.accept(filePath)) {
                            log.info(
                                    "Duplicate file ignored: " + filePath
                            );
                            return null;
                        }

                    } catch (IOException e) {

                        throw new RuntimeException(
                                "Duplicate check failed: " + filePath, e
                        );
                    }

                    try {

                        // Wait until file copying is completed
                        fileProcessingService.waitUntilStable(filePath);

                        // Capture signature BEFORE backup
                        FileDuplicateFilter.FileSignature signature =
                                fileDuplicateFilter.captureSignature(filePath);

                        // Launch Batch job
                        jobLauncherService.launchJob(filePath);

                        log.info(
                                "Spring Batch job submitted for: " + filePath
                        );

                    } catch (
                            org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException e
                    ) {

                        log.info(
                                "This CSV version was already processed. Skipping: "
                                        + filePath
                        );

                        try {
                            FileDuplicateFilter.FileSignature signature =
                                    fileDuplicateFilter.captureSignature(filePath);

                            fileDuplicateFilter.markProcessed(
                                    filePath, signature
                            );

                        } catch (IOException ioException) {

                            fileDuplicateFilter.release(filePath);

                            throw new RuntimeException(
                                    "Could not update processed file signature",
                                    ioException
                            );
                        }

                    } catch (Exception e) {

                        fileDuplicateFilter.release(filePath);

                        throw new RuntimeException(
                                "CSV processing could not be started: "
                                        + filePath, e
                        );
                    }

                    return null;
                })
                .get();
    }

    private boolean isAllowedFile(Path filePath) {

        String fileName = filePath.getFileName()
                .toString()
                .toLowerCase();

        return fileName.endsWith(".csv");
    }
}
