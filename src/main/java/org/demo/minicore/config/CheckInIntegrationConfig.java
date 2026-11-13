package org.demo.minicore.config;

import org.demo.minicore.service.CheckInService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

@Configuration
public class CheckInIntegrationConfig {

    @Bean()
    public MessageChannel checkInChannel(){
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow CheckInFlow(
            MessageChannel checkInChannel,
            CheckInService checkInService) {

        return IntegrationFlow
                .from(checkInChannel)

                // 3. Service Activator
                .handle(String.class, (name, headers) -> {

                    return checkInService
                            .checkInAndGetLogs(name);
                })
                .get();
    }
}
