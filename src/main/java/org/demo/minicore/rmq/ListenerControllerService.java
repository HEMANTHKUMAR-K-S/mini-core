
package org.demo.minicore.rmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageListenerContainer;
import org.springframework.amqp.core.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ListenerControllerService {

    private static final String LISTENER_ID = "RecordsListener";

    private final RabbitListenerEndpointRegistry registry;

    public ListenerControllerService(RabbitListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    public void stopListener() {

        MessageListenerContainer container =
                registry.getListenerContainer(LISTENER_ID);

        if (container != null) {
            container.stop();
            log.info("RabbitMQ listener paused");
        }
    }

    public void resumeListener() {

        org.springframework.amqp.core.MessageListenerContainer container =
                registry.getListenerContainer(LISTENER_ID);

        if (container != null) {
            container.start();
            log.info("RabbitMQ listener resumed");
        }
    }
}
