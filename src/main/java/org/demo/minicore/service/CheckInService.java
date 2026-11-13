package org.demo.minicore.service;

import jakarta.transaction.Transactional;
import org.demo.minicore.dto.CheckInResponse;
import org.demo.minicore.entity.Attendance;
import org.demo.minicore.entity.User;
import org.demo.minicore.repository.AttendanceRepo;
import org.demo.minicore.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckInService {


    private final UserRepository userRepository;

    private final AttendanceRepo attendanceRepo;

    public CheckInService(UserRepository userRepository,AttendanceRepo attendanceRepo){

        this.attendanceRepo=attendanceRepo;
        this.userRepository=userRepository;
    }



    @Transactional
    public CheckInResponse checkInAndGetLogs(String name) {

        // 1. Find employee using name
        User user = userRepository.findByName(name)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found: " + name));

        // 2. Get current check-in time
        LocalDateTime checkInTime = LocalDateTime.now();

        // 3. Create attendance record
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setCheckInTime(checkInTime);

        // 4. Save attendance record
        attendanceRepo.save(attendance);

        // 5. Retrieve all attendance logs for this employee
        List<org.demo.minicore.attendance.dto.LogResponse> logs =
                attendanceRepo
                        .findByUser_NameOrderByCheckInTimeDesc(name)
                        .stream()
                        .map(record ->
                                new org.demo.minicore.attendance.dto.LogResponse(
                                        record.getCheckInTime()))
                        .toList();

        // 6. Return response
        return new CheckInResponse(
                name,
                "CHECKED_IN",
                checkInTime,
                logs
        );
    }
}
