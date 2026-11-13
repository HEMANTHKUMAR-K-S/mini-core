package org.demo.minicore.watcher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

@Component
@EnableIntegration
public class DirectoryWatcher {

    @Value("${file.backupath}")
    private String backupPath;

    private final MessageChannel fileEventChannel;

    public DirectoryWatcher(
            @Qualifier("fileEventChannel")
            MessageChannel fileEventChannel) {

        this.fileEventChannel = fileEventChannel;
    }

    public void startWatching(String directoryPath) {

        try (WatchService watchService =
                     FileSystems.getDefault().newWatchService()) {

            Path directory = Path.of(directoryPath);

            directory.register(
                    watchService,
                    ENTRY_CREATE,
                    ENTRY_DELETE,
                    ENTRY_MODIFY
            );


            while (true) {

                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {

                    if (event.kind() == OVERFLOW) {
                        System.out.println("Some filesystem events may have been lost.");
                        continue;
                    }

                    Path fileName = (Path) event.context();

                    createDirectory();
                    Path fullPath = directory.resolve(fileName);

                    FileSystemEvent fileSystemEvent =
                            new FileSystemEvent(event.kind(), fullPath);

                    fileEventChannel.send(
                            MessageBuilder
                                    .withPayload(fileSystemEvent)
                                    .build()
                    );
                }

                if (!key.reset()) {
                    System.out.println("Directory registration is invalid.");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Watcher error: " + e.getMessage());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Watcher interrupted.");
        }
    }

    private void createDirectory(){

        Path filePath=Path.of(backupPath);

        if(!Files.exists(filePath)){
            try {
                Files.createDirectory(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}