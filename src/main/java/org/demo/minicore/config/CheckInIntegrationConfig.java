package org.demo.minicore.config;

import org.demo.minicore.service.CheckInService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.MessageChannel;

@Configuration
public class CheckInIntegrationConfig {

    @Bean()
    public MessageChannel checkInChannel(){
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow CheckInFlow() {

        return IntegrationFlow
                .from(
                        Http.inboundGateway("/check-in/{name}")
                                .requestMapping(m->m.methods(HttpMethod.POST))
                                .payloadExpression("#pathVariables.name")
                ).channel("checkInChannel")
                .get();
    }
}
