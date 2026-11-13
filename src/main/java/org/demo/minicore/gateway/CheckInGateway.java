package org.demo.minicore.gateway;


import org.demo.minicore.dto.CheckInResponse;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.zip.CheckedOutputStream;

@MessagingGateway
public interface CheckInGateway {

    @Gateway(requestChannel = "checkInChannel")
    CheckInResponse checkIn(String name);
}
