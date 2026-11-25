
package org.demo.minicore.service;

import org.demo.minicore.dto.CheckInResponse;
import org.demo.minicore.entity.Attendance;
import org.demo.minicore.entity.User;
import org.demo.minicore.repository.AttendanceRepo;
import org.demo.minicore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckInService {

    private final UserRepository userRepository;
    private final AttendanceRepo attendanceRepo;

    public CheckInService(
            UserRepository userRepository,
            AttendanceRepo attendanceRepo) {

        this.attendanceRepo = attendanceRepo;
        this.userRepository = userRepository;
    }

    // SYNCHRONOUS CHECK-IN
    @Transactional
    public CheckInResponse checkIn(String name) {

        // 1. Validate employee
        User user = userRepository.findByName(name)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found: " + name));

        // 2. Check whether employee already has an active check-in
        boolean alreadyCheckedIn = attendanceRepo
                .findFirstByUser_NameAndCheckOutTimeIsNullOrderByCheckInTimeDesc(name)
                .isPresent();

        if (alreadyCheckedIn) {
            throw new RuntimeException(
                    "Employee is already checked in: " + name);
        }

        // 3. Capture check-in time
        LocalDateTime checkInTime = LocalDateTime.now();

        // 4. Create attendance record
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setCheckInTime(checkInTime);

        // 5. Save attendance
        attendanceRepo.save(attendance);

        // 6. Return check-in response without logs
        return new CheckInResponse(
                name,
                "CHECKED_IN",
                checkInTime,
                List.of()
        );
    }

    // ASYNCHRONOUS LOG RETRIEVAL
    @Transactional(readOnly = true)
    public List<org.demo.minicore.dto.LogResponse>
    getLogs(String name) {

        return attendanceRepo
                .findByUser_NameOrderByCheckInTimeDesc(name)
                .stream()
                .map(record ->
                        new org.demo.minicore.dto.LogResponse(
                                record.getCheckInTime()))
                .toList();
    }
}
