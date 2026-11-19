package org.demo.minicore.rmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
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

    log.info("Message sent successfully!");
    log.info("Exchange: " + exchangeName);
    log.info("Routing Key: " + username);
    log.info("Message: " + msg);

}
}
