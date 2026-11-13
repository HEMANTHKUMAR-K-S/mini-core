package org.demo.minicore.controller;

import org.demo.minicore.dto.CheckInRequest;
import org.demo.minicore.dto.CheckInResponse;
import org.demo.minicore.gateway.CheckInGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CheckInController {

    private final CheckInGateway checkInGateway;

    public CheckInController(CheckInGateway checkInGateway) {
        this.checkInGateway = checkInGateway;
    }


    @PostMapping("/checkIn")
    public ResponseEntity<CheckInResponse> checkIn(@RequestBody CheckInRequest request){

        CheckInResponse response =checkInGateway.checkIn(request.getName());

        return ResponseEntity.ok(response);
    }

}
