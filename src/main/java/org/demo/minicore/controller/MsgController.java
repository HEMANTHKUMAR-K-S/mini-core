package org.demo.minicore.controller;

import org.demo.minicore.rmq.msgProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MsgController {

    private final msgProducer msgProducer;

    public MsgController(msgProducer msgProducer){
        this.msgProducer=msgProducer;
    }


    @PostMapping("/send/{username}")
    public ResponseEntity<String> sendMessage(@PathVariable String username, @RequestBody String msg){

        msgProducer.sendMessage(username,msg);

        return ResponseEntity.ok(
                "Message sent to user: " + username
        );

    }
}
