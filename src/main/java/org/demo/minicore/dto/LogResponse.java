package org.demo.minicore.attendance.dto;

import java.time.LocalDateTime;

public class LogResponse {

    private LocalDateTime checkInTime;

    public LogResponse(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
}