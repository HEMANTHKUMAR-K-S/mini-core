package org.demo.minicore.watcher;

import org.demo.minicore.config.MultiProcessorTaskExecutor;
import org.demo.minicore.repository.AttendanceRepo;
import org.demo.minicore.repository.UserRepository;
import org.demo.minicore.service.CheckOutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
public class CheckOutServiceActivator {

    private static final Logger log =
            LoggerFactory.getLogger(CheckOutServiceActivator.class);

    private final CheckOutService checkOutService;
    private final UserRepository userRepository;
    private final AttendanceRepo attendanceRepo;
    private final MultiProcessorTaskExecutor taskExecutor;

    public CheckOutServiceActivator(
            CheckOutService checkOutService,
            UserRepository userRepository,
            AttendanceRepo attendanceRepo,
            MultiProcessorTaskExecutor taskExecutor) {

        this.checkOutService = checkOutService;
        this.userRepository = userRepository;
        this.attendanceRepo = attendanceRepo;
        this.taskExecutor = taskExecutor;
    }

    @ServiceActivator(inputChannel = "checkOutChannel")
    public String checkOutProcess(String name) {

        // Step 1: Check whether employee exists
        userRepository.findByName(name)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found: " + name));

        // Step 2: Check whether employee has an active check-in
        boolean hasActiveCheckIn = attendanceRepo
                .findFirstByUser_NameAndCheckOutTimeIsNullOrderByCheckInTimeDesc(name)
                .isPresent();

        if (!hasActiveCheckIn) {
            throw new IllegalArgumentException(
                    "Employee has already checked out or has not checked in: "
                            + name);
        }

        // Step 3: Submit task to thread pool
        taskExecutor.execute(() -> {
            try {
                checkOutService.checkOut(name);

                log.info("Checkout completed for employee: {}", name);

            } catch (Exception ex) {
                log.error("Asynchronous checkout failed for employee: {}",
                        name, ex);
            }
        });

        // Step 4: Return immediate acknowledgement
        return "Checkout request accepted for: " + name;
    }
}