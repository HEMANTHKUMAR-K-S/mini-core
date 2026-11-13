package org.demo.minicore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FileProcessingService {


    @Value("${file.backupath}")
    private String backupPath;
    public void process(File file) throws IOException {
        System.out.println("detecting the files");
        System.out.println("File Name : " + file.getName());
        System.out.println("File Path : " + file.getAbsolutePath());
        System.out.println("File Size : " + file.length() + " bytes");

        Path backupFolder=Path.of(backupPath);

        Path backupFile=backupFolder.resolve(file.getName());

        Files.copy(file.toPath(),backupFile, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Processing Status : SUCCESS");
        System.out.println("Backup Location   : " + backupFile);
    }
}
