package org.demo.minicore.repository;

import org.demo.minicore.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepo extends JpaRepository<Attendance, Long> {

    List<Attendance> findByUser_NameOrderByCheckInTimeDesc(String name);

    // Find the latest attendance record without checkout
    Optional<Attendance> findFirstByUser_NameAndCheckOutTimeIsNullOrderByCheckInTimeDesc(
            String name
    );
}