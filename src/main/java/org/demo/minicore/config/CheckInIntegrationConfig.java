
package org.demo.minicore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.MessageChannel;

@Configuration
public class CheckInIntegrationConfig {

    // Check-in channel
    @Bean
    public MessageChannel checkInChannel() {
        return new DirectChannel();
    }

    // Check-out channel
    @Bean
    public MessageChannel checkOutChannel() {
        return new DirectChannel();
    }

    // Check-in HTTP flow
    @Bean
    public IntegrationFlow checkInFlow() {

        return IntegrationFlow
                .from(
                        Http.inboundGateway("/check-in/{name}")
                                .requestMapping(mapping ->
                                        mapping.methods(HttpMethod.POST))
                                .payloadExpression("#pathVariables['name']")
                )
                .channel("checkInChannel")
                .get();
    }

    // Check-out HTTP flow
    @Bean
    public IntegrationFlow checkOutFlow() {

        return IntegrationFlow
                .from(
                        Http.inboundGateway("/check-out/{name}")
                                .requestMapping(mapping ->
                                        mapping.methods(HttpMethod.POST))
                                .payloadExpression("#pathVariables['name']")
                )
                .channel("checkOutChannel")
                .get();
    }
}
