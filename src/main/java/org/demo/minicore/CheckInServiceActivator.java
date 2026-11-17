package org.demo.minicore;

import org.demo.minicore.dto.CheckInResponse;
import org.demo.minicore.service.CheckInService;
import org.demo.minicore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
public class CheckInServiceActivator {

    private final CheckInService checkInService;
    private final UserRepository userRepository;

    private final ThreadPoolTaskExecutor checkInExecutor;


    private static final Logger log = LoggerFactory.getLogger(CheckInServiceActivator.class);

    public CheckInServiceActivator(CheckInService checkInService, UserRepository userRepository, @Qualifier("checkInExecutor")ThreadPoolTaskExecutor checkInExecutor) {

        this.checkInService = checkInService;
        this.userRepository = userRepository;
        this.checkInExecutor = checkInExecutor;
    }


    @ServiceActivator(inputChannel = "checkInChannel")
    public String checkInProcess(String name) {

        // 1. Actual check-in happens synchronously
        CheckInResponse response = checkInService.checkIn(name);

        log.info(
                "Employee {} checked in successfully at {}",
                name,
                response.getCheckInTime()
        );

        // 2. Retrieve logs asynchronously
        checkInExecutor.execute(() -> {

            try {

                log.info(
                        "Thread: {} | Employee: {} | Total logs: {}",
                        Thread.currentThread().getName(),
                        name
//                        logs.size()
                );

            } catch (Exception ex) {

                log.error(
                        "Failed to retrieve logs for employee: {}",
                        name,
                        ex
                );
            }
        });

        // 3. Return immediately after check-in and task submission
        return "Check-in successful for employee: " + name;
    }

}
