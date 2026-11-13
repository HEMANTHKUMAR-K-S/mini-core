package org.demo.minicore.repository;

import org.demo.minicore.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepo extends JpaRepository<Attendance, Long> {

    List<Attendance> findByUser_NameOrderByCheckInTimeDesc(String name);

}