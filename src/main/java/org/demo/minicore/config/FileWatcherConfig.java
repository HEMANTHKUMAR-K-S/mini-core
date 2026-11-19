
package org.demo.minicore.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.demo.minicore.watcher.DirectoryWatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Configuration
public class FileWatcherConfig {

    @Value("${file.inputpath}")
    private String inputPath;

    @Value("${file.backupath}")
    private String backupPath;

    private final DirectoryWatcher directoryWatcher;

    public FileWatcherConfig(DirectoryWatcher directoryWatcher) {
        this.directoryWatcher = directoryWatcher;
    }

    @PostConstruct
    public void createDirectories() throws IOException {

        Path inputFolder = Path.of(inputPath)
                .toAbsolutePath().normalize();

        Path backupFolder = Path.of(backupPath)
                .toAbsolutePath().normalize();

        if (inputFolder.equals(backupFolder)) {
            throw new IllegalStateException(
                    "Input and backup directories cannot be the same"
            );
        }

        Files.createDirectories(inputFolder);
        Files.createDirectories(backupFolder);
        log.info("Input Directory  : " + inputFolder);
        log.info("Backup Directory: " + backupFolder);
    }

    @Bean
    public ApplicationRunner startDirectoryWatcher() {

        return args -> {

            Thread watcherThread = new Thread(
                    () -> directoryWatcher.startWatching(inputPath)
            );

            watcherThread.setName("Filewatcher");
            watcherThread.setDaemon(true);
            watcherThread.start();

            log.info("Directory watcher started");
        };
    }
}
