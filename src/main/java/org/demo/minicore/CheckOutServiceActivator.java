package org.demo.minicore;

import org.demo.minicore.dto.UserLogsResoponse;
import org.demo.minicore.repository.AttendanceRepo;
import org.demo.minicore.repository.UserRepository;
import org.demo.minicore.rmq.MsgProducer;
import org.demo.minicore.service.CheckInService;
import org.demo.minicore.service.CheckOutService;
import org.demo.minicore.service.UserLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class CheckOutServiceActivator {


 private final MsgProducer msgProducer;

    public CheckOutServiceActivator(MsgProducer msgProducer) {
        this.msgProducer = msgProducer;
    }


    @ServiceActivator(inputChannel = "checkOutChannel")

    public UserLogsResoponse checkIn(String username){

        msgProducer.sendMessage(username,false);


        return new UserLogsResoponse(
                "Check-out successful",
                username,
                "CHECK-OUT"
        );

    }






























    // iNTEGRATION WITH THE THREAD POOL LOGIC


//    private static final Logger log =
//            LoggerFactory.getLogger(CheckOutServiceActivator.class);
//
//    private final CheckOutService checkOutService;
//    private final CheckInService checkInService;
//    private final UserRepository userRepository;
//    private final AttendanceRepo attendanceRepo;
//    private final ThreadPoolTaskExecutor checkOutExecutor;
//
//    public CheckOutServiceActivator(
//            CheckOutService checkOutService,
//            CheckInService checkInService,
//            UserRepository userRepository,
//            AttendanceRepo attendanceRepo,
//            @Qualifier("checkOutExecutor")
//            ThreadPoolTaskExecutor checkOutExecutor) {
//
//        this.checkOutService = checkOutService;
//        this.checkInService = checkInService;
//        this.userRepository = userRepository;
//        this.attendanceRepo = attendanceRepo;
//        this.checkOutExecutor = checkOutExecutor;
//    }
//
//    @ServiceActivator(inputChannel = "checkOutChannel")
//    public String checkOutProcess(String name) {
//
//        // Step 1: Validate employee
//        userRepository.findByName(name)
//                .orElseThrow(() ->
//                        new IllegalArgumentException(
//                                "Employee not found: " + name));
//
//        // Step 2: Check whether employee has an active check-in
//        boolean hasActiveCheckIn = attendanceRepo
//                .findFirstByUser_NameAndCheckOutTimeIsNullOrderByCheckInTimeDesc(name)
//                .isPresent();
//
//        if (!hasActiveCheckIn) {
//            throw new IllegalArgumentException(
//                    "Employee has already checked out or has not checked in: "
//                            + name);
//        }
//
//        // Step 3: Perform actual checkout synchronously
//        checkOutService.checkOut(name);
//
//        log.info(
//                "Checkout completed synchronously | Employee: {} | Thread: {}",
//                name,
//                Thread.currentThread().getName()
//        );

        // Step 4: Retrieve employee logs asynchronously
//        checkOutExecutor.execute(() -> {
//            try {
//
//                log.info(
//                        "Checkout logs retrieved | Employee: {} | Thread: {} | Total logs: {}",
//                        name,
//                        Thread.currentThread().getName()
//                );
//
//            } catch (Exception ex) {
//                log.error(
//                        "Asynchronous checkout log retrieval failed for employee: {}",
//                        name,
//                        ex
//                );
//            }
//        });

        // Step 5: Return acknowledgement
//        return "Checkout successful for: " + name;
//    }



}