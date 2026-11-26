package org.demo.minicore.rmq;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class rmqServices {

    private final AmqpAdmin amqpAdmin;

    @Value("${rabbitmq.queue.name}")
    private String checkInQueueName;

    public rmqServices(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    @PostConstruct
    public void initializeCheckInQueue() {

        Queue queue = new Queue(checkInQueueName, true);

        amqpAdmin.declareQueue(queue);

        log.info("queue created: {}", checkInQueueName);
    }
}