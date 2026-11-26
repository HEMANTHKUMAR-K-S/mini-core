
package org.demo.minicore.rmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MsgProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.name}")
    private String QueueName;

    public MsgProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String username, boolean check) {

        rabbitTemplate.convertAndSend(
                "",
                QueueName,
                username,
                message -> {
                    message.getMessageProperties()
                            .setHeader("check", check);
                    return message;
                }
        );

        log.info("message published to RabbitMQ");
        log.info("Queue: {}", QueueName);
        log.info("Username: {}", username);
        log.info("Operation: {}", check ? "CHECK-IN" : "CHECK-OUT");
    }
}
