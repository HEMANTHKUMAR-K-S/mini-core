package org.demo.minicore.watcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public record FileSystemEvent(
        WatchEvent.Kind<?> eventkind,
        Path filePath
) {
}
