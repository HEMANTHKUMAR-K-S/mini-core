package org.demo.minicore.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;

@Configuration
public class CheckInOutboundConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    public IntegrationFlow checkInOutboundFlow(RabbitTemplate rabbitTemplate){


        return IntegrationFlow.from("checkInChannel")
                .handle(Amqp.outboundAdapter(rabbitTemplate).exchangeName(exchange).routingKeyExpression("payload")).get();
    }

}
