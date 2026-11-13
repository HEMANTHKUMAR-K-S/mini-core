package org.demo.minicore.config;


import jakarta.annotation.PostConstruct;
import org.demo.minicore.service.FileProcessingService;
import org.demo.minicore.watcher.DirectoryWatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class FileWatcherConfig {

    @Value("${file.inputpath}")
    private String inputPath;

    @Value("${file.backupath}")
    private String backupPath;

    private final FileProcessingService fileProcessingService;
    private final DirectoryWatcher directoryWatcher;

    public FileWatcherConfig(
            FileProcessingService fileProcessingService,
            DirectoryWatcher directoryWatcher) {

        this.fileProcessingService = fileProcessingService;
        this.directoryWatcher = directoryWatcher;
    }

    @PostConstruct
    public void createDirectories() throws IOException {

        Path inputFolder = Path.of(inputPath);
        Path backupFolder = Path.of(backupPath);

        if (!Files.exists(inputFolder)) {
            Files.createDirectories(inputFolder);
        }

        if (!Files.exists(backupFolder)) {
            Files.createDirectories(backupFolder);
        }
    }

    @Bean
    public ApplicationRunner startDirectoryWatcher() {

        return args -> {

            Thread watcherThread = new Thread(
                    () -> directoryWatcher.startWatching(inputPath)
            );

            watcherThread.setName("input-directory-watcher");
            watcherThread.setDaemon(true);
            watcherThread.start();
        };
    }
}
