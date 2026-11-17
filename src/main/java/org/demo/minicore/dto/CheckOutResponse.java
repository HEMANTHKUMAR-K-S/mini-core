package org.demo.minicore.dto;

import java.time.LocalDateTime;

public class CheckOutResponse {

    private String name;
    private String status;
    private LocalDateTime checkOutTime;

    public CheckOutResponse(
            String name,
            String status,
            LocalDateTime checkOutTime) {

        this.name = name;
        this.status = status;
        this.checkOutTime = checkOutTime;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }
}