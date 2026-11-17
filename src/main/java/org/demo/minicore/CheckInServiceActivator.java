
package org.demo.minicore;

import org.demo.minicore.service.CheckInService;
import org.demo.minicore.config.MultiProcessorTaskExecutor;
import org.demo.minicore.repository.UserRepository;
import org.demo.minicore.watcher.CheckOutServiceActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class CheckInServiceActivator {

    private final CheckInService checkInService;
    private final UserRepository userRepository;
    private final MultiProcessorTaskExecutor taskExecutor;


    private static final Logger log =
            LoggerFactory.getLogger(CheckInServiceActivator.class);

    public CheckInServiceActivator(
            CheckInService checkInService,
            UserRepository userRepository,
            MultiProcessorTaskExecutor taskExecutor) {

        this.checkInService = checkInService;
        this.userRepository = userRepository;
        this.taskExecutor = taskExecutor;
    }

    @ServiceActivator(inputChannel = "checkInChannel")
    public String checkInProcess(String name) {

        // 1. Validate employee before submitting task
        userRepository.findByName(name)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found: " + name));

        // 2. Submit attendance processing to executor
        taskExecutor.execute(() -> {
            try {
                checkInService.checkInAndGetLogs(name);// task/work arrive here to executor
                log.info("Thread: {} | Employee: {} | Check-in at: {}", Thread.currentThread().getName(), name, LocalDateTime.now());
            } catch (Exception ex) {
                System.err.println(
                        "Check-in failed for: " + name + " | " + ex.getMessage());
            }
        });

        // 3. Return acknowledgement
        return "Check-in request accepted for: " + name;
    }
}
