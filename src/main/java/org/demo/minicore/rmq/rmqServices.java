
package org.demo.minicore.rmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class rmqServices {

    private final AmqpAdmin amqpAdmin;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.exchange.type}")
    private String exchangeType;

    public rmqServices(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    // Create the common exchange
    public void createExchange() {

        DirectExchange exchange =
                new DirectExchange(exchangeName, true, false);

        amqpAdmin.declareExchange(exchange);

//        log.info("Common exchange created: " + exchangeName);
    }

    // Create a queue for each user
    public void createUser(String userName) {

        String queueName = "core." + userName + ".queue";

        Queue queue = new Queue(queueName, true);

        DirectExchange exchange =
                new DirectExchange(exchangeName, true, false);

        Binding binding = BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(userName);

        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(binding);

        log.info("Created queue: " + queueName);
        log.info("Bound to exchange: " + exchangeName);
        log.info("Routing key: " + userName);
    }

    // Delete only the user's queue
    public void deleteUser(String username) {

        String queueName = "core." + username + ".queue";

        amqpAdmin.deleteQueue(queueName);

        log.info("Deleted queue: " + queueName);
    }

    // Update username: create new queue, then remove old queue
    public void updateuser(String oldUser, String newUser) {

        if (oldUser.equals(newUser)) {
            return;
        }

        createUser(newUser);

        deleteUser(oldUser);

        log.info("Updated queue from " + oldUser + " to " + newUser);
    }

}
