package org.demo.minicore.rmq;


import lombok.extern.slf4j.Slf4j;
import org.demo.minicore.entity.UserLogs;
import org.demo.minicore.service.UserLogService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
public class MsgConsumer {

    private final UserLogService userLogService;


    public MsgConsumer(UserLogService userLogService) {
        this.userLogService = userLogService;
    }


    @RabbitListener(   id = "RecordsListener",queues = "${rabbitmq.queue.name}")
    public void receiveMessage(Message messages) {

            String username = new String(
                    messages.getBody(),
                    java.nio.charset.StandardCharsets.UTF_8
            );
            Object headerValue = messages.getMessageProperties()
                    .getHeaders()
                    .get("check");
            if (!(headerValue instanceof Boolean check)) {
                throw new IllegalArgumentException(
                        "Invalid or missing check header"
                );
            }
            if (check) {

//                log.info("Processing CHECK-IN for: {}", username);

                userLogService.saveLogs(username, "CHECK-IN");

            } else {

//                log.info("Processing CHECK-OUT for: {}", username);

                userLogService.saveLogs(username, "CHECK-OUT");
            }



    }
}
