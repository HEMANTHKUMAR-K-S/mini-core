
package org.demo.minicore.watcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

@Slf4j
@Component
public class DirectoryWatcher {

    private final MessageChannel fileEventChannel;

    public DirectoryWatcher(
            @Qualifier("fileEventChannel")
            MessageChannel fileEventChannel) {

        this.fileEventChannel = fileEventChannel;
    }

    public void startWatching(String directoryPath) {

        Path directory = Path.of(directoryPath)
                .toAbsolutePath()
                .normalize();

        try (WatchService watchService =
                     FileSystems.getDefault().newWatchService()) {

            if (!Files.isDirectory(directory)) {
                throw new IOException(
                        "Input directory does not exist: " + directory
                );
            }

            directory.register(
                    watchService,
                    ENTRY_CREATE,
                    ENTRY_MODIFY
            );

           log.info("Watching directory: " + directory);

            while (!Thread.currentThread().isInterrupted()) {

                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {

                    if (event.kind() == OVERFLOW) {
                       log.info(
                                "Some filesystem events may have been lost"
                        );
                        continue;
                    }

                    Path fileName = (Path) event.context();

                    Path fullPath = directory.resolve(fileName)
                            .toAbsolutePath()
                            .normalize();

                    if (Files.isDirectory(fullPath)) {
                        continue;
                    }

                    FileSystemEvent fileSystemEvent =
                            new FileSystemEvent(
                                    event.kind(),
                                    fullPath
                            );

                    try {

                        fileEventChannel.send(
                                MessageBuilder
                                        .withPayload(fileSystemEvent)
                                        .build()
                        );

                       log.info(
                                "Event published: " + fullPath
                        );

                    } catch (RuntimeException e) {

                     log.error(
                                "Event processing failed: " + e.getMessage()
                        );
                    }
                }

                if (!key.reset()) {
                   log.info(
                            "Directory registration is invalid"
                    );
                    break;
                }
            }

        } catch (IOException e) {

    
        log.error(
                    "Watcher error: " + e.getMessage()
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

           log.info("Watcher interrupted");
        }
    }
}
