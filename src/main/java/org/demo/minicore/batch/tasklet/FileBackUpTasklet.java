package org.demo.minicore.batch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.demo.minicore.service.FileProcessingService;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Primary;

import java.nio.file.Path;

@Slf4j
public class FileBackUpTasklet implements Tasklet {

 private final FileProcessingService fileProcessingService;
 private final String inputFile;

    public FileBackUpTasklet(FileProcessingService fileProcessingService, String inputFile) {
        this.fileProcessingService = fileProcessingService;
        this.inputFile = inputFile;
    }

    @Override
    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        Path path=Path.of(inputFile);
       log.info("starting backup..😊😊");
        fileProcessingService.backup(path);
       log.info("backup Completed .. 👌👌👌");
        return RepeatStatus.FINISHED;
    }
}
