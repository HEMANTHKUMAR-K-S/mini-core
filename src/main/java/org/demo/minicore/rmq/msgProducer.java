package org.demo.minicore.rmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class msgProducer {

private final RabbitTemplate rabbitTemplate;
@Value("${rabbitmq.exchange.name}")
private String exchangeName;

public msgProducer(RabbitTemplate rabbitTemplate){

    this.rabbitTemplate=rabbitTemplate;
}

public void sendMessage(String username,String msg){

    rabbitTemplate.convertAndSend(exchangeName,username,msg);

    System.out.println("Message sent successfully!");
    System.out.println("Exchange: " + exchangeName);
    System.out.println("Routing Key: " + username);
    System.out.println("Message: " + msg);

}
}
