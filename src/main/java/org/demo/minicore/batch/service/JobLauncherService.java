
package org.demo.minicore.batch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class JobLauncherService {

    private final JobOperator jobOperator;
    private final Job userJob;

    public JobLauncherService(JobOperator jobOperator, Job userJob) {
        this.jobOperator = jobOperator;
        this.userJob = userJob;
    }


    public void launchJob(Path filePath) throws Exception {

        Path normalizedPath = filePath.toAbsolutePath().normalize();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("inputFile", normalizedPath.toString())
                .addLong("fileSize", Files.size(normalizedPath))
                .addLong(
                        "lastModified",
                        Files.getLastModifiedTime(normalizedPath).toMillis()
                )
                .toJobParameters();
        log.info("Launching Spring Batch job...");
        log.info("Input file: " + normalizedPath);

        jobOperator.start(userJob, jobParameters);
    }


//    public void launchJob(Path filePath) throws Exception {
//
//        Path normalizedPath = filePath.toAbsolutePath().normalize();
//
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("inputFile", normalizedPath.toString())
//                .addLong("fileSize", Files.size(normalizedPath))
//                .addLong(
//                        "lastModified",
//                        Files.getLastModifiedTime(normalizedPath).toMillis()
//                )
//                .toJobParameters();
//
//        log.info("Launching Spring Batch job...");
//        log.info("Input file: " + normalizedPath);
//
//        try {
//            var execution = jobOperator.start(userJob, jobParameters);
//
//            log.info("Job execution started successfully.");
//            log.info("Job execution ID: " + execution.getId());
//            log.info("Job status: " + execution.getStatus());
//
//        } catch (Exception e) {
//            System.err.println("========== JOB LAUNCH FAILED ==========");
//            e.printStackTrace();
//            throw e;
//        }


}
