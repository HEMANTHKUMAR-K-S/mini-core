
package org.demo.minicore.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FileProcessingService {



    // VM arguments
    @Value("${file.backupath}")
    private String backupPath;

    @Value("${file.stability-check-interval-ms:1000}")
    private long checkInterval;

    @Value("${file.stability-required-checks:2}")
    private int requiredStableChecks;

    @Value("${file.stability-timeout-ms:60000}")
    private long stabilityTimeout;

    // STEP 1: Wait until the file is completely stable
    public void waitUntilStable(Path filePath) throws IOException {

        Path sourceFile = filePath.toAbsolutePath().normalize();
        log.info("Waiting for file stability: " + sourceFile);

        long deadline = System.nanoTime()
                + TimeUnit.MILLISECONDS.toNanos(stabilityTimeout);

        long previousSize = -1;
        FileTime previousModified = null;
        int stableChecks = 0;

        while (System.nanoTime() < deadline) {

            if (Files.isRegularFile(sourceFile)) {

                long currentSize = Files.size(sourceFile);

                FileTime currentModified =
                        Files.getLastModifiedTime(sourceFile);

                if (currentSize == previousSize
                        && currentModified.equals(previousModified)) {

                    stableChecks++;

                } else {
                    stableChecks = 0;
                }

                if (stableChecks >= requiredStableChecks) {

                    log.info("File is stable: " + sourceFile);
                    return;
                }

                previousSize = currentSize;
                previousModified = currentModified;
            }

            try {

                Thread.sleep(checkInterval);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                throw new IOException(
                        "Interrupted while waiting for file stability",
                        e
                );
            }
        }

        throw new IOException(
                "File did not become stable within "
                        + stabilityTimeout + " ms: " + sourceFile
        );
    }

    // STEP 2: Backup the file after successful Batch import
    public void backup(Path filePath) throws IOException {

        Path sourceFile = filePath.toAbsolutePath().normalize();

        if (!Files.isRegularFile(sourceFile)) {
            throw new IOException("Source file does not exist: " + sourceFile);
        }

        Path backupFolder = Path.of(backupPath)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(backupFolder);

        Path backupFile = backupFolder.resolve(
                sourceFile.getFileName()
        );

        Path temporaryBackup = backupFolder.resolve(
                sourceFile.getFileName() + ".part"
        );

        log.info("Starting CSV backup: " + sourceFile);

        try {

            Files.move(
                    sourceFile,
                    temporaryBackup,
                    StandardCopyOption.REPLACE_EXISTING
            );
            try {

                Files.move(
                        temporaryBackup,
                        backupFile,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );

            } catch (AtomicMoveNotSupportedException e) {

                Files.move(
                        temporaryBackup,
                        backupFile,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } catch (IOException e) {

            Files.deleteIfExists(temporaryBackup);
            throw e;
        }

        log.info("Backup completed successfully!");
        log.info("Backup Location: " + backupFile);
    }
}
