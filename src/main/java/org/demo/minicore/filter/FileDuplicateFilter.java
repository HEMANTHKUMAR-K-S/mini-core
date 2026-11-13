package org.demo.minicore.filter;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class FileDuplicateFilter {

    private final Set<Path> processedFiles= ConcurrentHashMap.newKeySet();

    public boolean accept(Path filePath) {

        Path normalizedPath =
                filePath.toAbsolutePath().normalize();

        return processedFiles.add(normalizedPath);
    }
}
