
package org.demo.minicore.filter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FileDuplicateFilter {

    private final Set<Path> processingFiles =
            ConcurrentHashMap.newKeySet();

    private final Map<Path, FileSignature> processedFiles =
            new ConcurrentHashMap<>();

    // Check whether the file should be processed
    public boolean accept(Path filePath) throws IOException {

        Path normalizedPath = normalize(filePath);

        // Prevent simultaneous processing of the same path
        if (!processingFiles.add(normalizedPath)) {
            return false;
        }

        try {

            if (Files.isRegularFile(normalizedPath)) {

                FileSignature currentSignature =
                        getSignature(normalizedPath);

                FileSignature previousSignature =
                        processedFiles.get(normalizedPath);

                // Ignore unchanged files
                if (currentSignature.equals(previousSignature)) {

                    processingFiles.remove(normalizedPath);

                    return false;
                }
            }

            return true;

        } catch (IOException e) {

            processingFiles.remove(normalizedPath);

            throw e;
        }
    }

    // Capture file signature before backup
    public FileSignature captureSignature(Path filePath)
            throws IOException {

        return getSignature(normalize(filePath));
    }

    // Mark file as successfully processed
    public void markProcessed(
            Path filePath,
            FileSignature signature) {

        Path normalizedPath = normalize(filePath);

        processedFiles.put(normalizedPath, signature);

        processingFiles.remove(normalizedPath);
    }

    // Release file lock when processing fails
    public void release(Path filePath) {

        processingFiles.remove(normalize(filePath));
    }

    // Normalize file path
    private Path normalize(Path filePath) {

        return filePath.toAbsolutePath().normalize();
    }

    // Read file metadata
    private FileSignature getSignature(Path filePath)
            throws IOException {

        return new FileSignature(
                Files.size(filePath),
                Files.getLastModifiedTime(filePath)
        );
    }

    // File signature model
    public record FileSignature(
            long size,
            FileTime lastModified
    ) {
    }
}
