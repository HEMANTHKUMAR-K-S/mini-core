package org.demo.minicore.config;

import org.demo.minicore.filter.FileDuplicateFilter;
import org.demo.minicore.service.FileProcessingService;
import org.demo.minicore.watcher.FileSystemEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.integration.annotation.EnableIntegration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Configuration
@org.springframework.integration.config.EnableIntegration
public class FileIntegrationConfig {

    @Bean
    public MessageChannel fileEventChannel() {
        return new DirectChannel();
    }

    @Bean
    public FileDuplicateFilter fileDuplicateFilter(){
        return new FileDuplicateFilter();
    }
    @Bean
    public IntegrationFlow fileEventFlow(
            @Qualifier("fileEventChannel") MessageChannel channel,
            FileProcessingService fileProcessingService,
            FileDuplicateFilter fileDuplicateFilter) {

        return IntegrationFlow
                .from(channel)
                .handle(FileSystemEvent.class, (event, headers) -> {

                    Path filePath = event.filePath()
                            .toAbsolutePath()
                            .normalize();

                    System.out.println("SPRING INTEGRATION RECEIVED EVENT");
                    System.out.println("Event Type : " + event.eventkind().name());
                    System.out.println("File Path  : " + filePath);

                    // Accept only newly created files
                    if (event.eventkind() != ENTRY_CREATE) {
                        return null;
                    }

                    // Accept only CSV and TXT files
                    if (!isAllowedFile(filePath.toString())) {
                        return null;
                    }

                    // Prevent duplicate processing
                    if (!fileDuplicateFilter.accept(filePath)) {
                        System.out.println("Duplicate file ignored: " + filePath);
                        return null;
                    }

                    if (Files.isRegularFile(filePath)) {
                        try {
                            fileProcessingService.process(filePath.toFile());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    return null;
                })
                .get();
    }

    private boolean isAllowedFile(String fileName) {

        String lowerCaseName = fileName.toLowerCase();

        return lowerCaseName.endsWith(".csv")
                || lowerCaseName.endsWith(".txt");
    }
}