
package org.demo.minicore.service;

import jakarta.transaction.Transactional;
import org.demo.minicore.dto.CheckOutResponse;
import org.demo.minicore.entity.Attendance;
import org.demo.minicore.repository.AttendanceRepo;
import org.demo.minicore.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CheckOutService {

    private final AttendanceRepo attendanceRepo;
    private final UserRepository userRepository;

    public CheckOutService(
            AttendanceRepo attendanceRepo,
            UserRepository userRepository) {

        this.attendanceRepo = attendanceRepo;
        this.userRepository = userRepository;
    }

    @Transactional
    public CheckOutResponse checkOut(String name) {

        // 1. First validate whether employee exists
        userRepository.findByName(name)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found: " + name));

        // 2. Find the latest active attendance
        Attendance attendance = attendanceRepo
                .findFirstByUser_NameAndCheckOutTimeIsNullOrderByCheckInTimeDesc(name)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee has not checked in or is already checked out: "
                                        + name));

        // 3. Capture checkout time
        LocalDateTime checkOutTime = LocalDateTime.now();

        // 4. Update attendance
        attendance.setCheckOutTime(checkOutTime);

        // 5. Save updated attendance
        attendanceRepo.save(attendance);

        // 6. Return response
        return new CheckOutResponse(
                name,
                "CHECKED_OUT",
                checkOutTime
        );
    }
}
