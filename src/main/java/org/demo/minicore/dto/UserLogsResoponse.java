package org.demo.minicore.dto;

public class UserLogsResoponse {

    private String message;
    private String username;
    private String status;

    public UserLogsResoponse(String message, String username, String status) {
        this.message = message;
        this.username = username;
        this.status = status;
    }
    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }
}