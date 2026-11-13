package org.demo.minicore;


import org.demo.minicore.dto.CheckInResponse;
import org.demo.minicore.service.CheckInService;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
public class CheckInServiceActivator {


    private final CheckInService checkInService;

    public CheckInServiceActivator(CheckInService checkInService) {
        this.checkInService = checkInService;
    }


    @ServiceActivator(inputChannel = "checkInChannel")
    public CheckInResponse checkInProcess(String name){

        return checkInService.checkInAndGetLogs(name);
    }
}
