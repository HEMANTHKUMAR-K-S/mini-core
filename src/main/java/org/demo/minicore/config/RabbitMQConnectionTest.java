package org.demo.minicore.config;

import org.demo.minicore.rmq.rmqServices;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConnectionTest implements CommandLineRunner {


//    private final RabbitTemplate rabbitTemplate;
    private final rmqServices rmqServices;

    public RabbitMQConnectionTest(rmqServices rmqservices) {
        this.rmqServices = rmqservices;
    }

//    public RabbitMQConnectionTest(RabbitTemplate rabbitTemplate) {
//        this.rabbitTemplate = rabbitTemplate;
//    }

    @Override
    public void run(String... args) {

//        rmqServices.createUser("hemanth");

//        Boolean connected = rabbitTemplate.execute(
//                channel -> channel.isOpen()
//        );

//        System.out.println("RabbitMQ Connection Status: " + connected);
    }
}