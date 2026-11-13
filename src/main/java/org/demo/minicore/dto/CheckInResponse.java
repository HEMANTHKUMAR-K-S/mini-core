package org.demo.minicore.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CheckInResponse {

    private String name;
    private String status;
    private LocalDateTime checkInTime;
    private List<org.demo.minicore.attendance.dto.LogResponse> logs;

    public CheckInResponse(
            String name,
            String status,
            LocalDateTime checkInTime,
            List<org.demo.minicore.attendance.dto.LogResponse> logs) {

        this.name = name;
        this.status = status;
        this.checkInTime = checkInTime;
        this.logs = logs;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public List<org.demo.minicore.attendance.dto.LogResponse> getLogs() {
        return logs;
    }
}