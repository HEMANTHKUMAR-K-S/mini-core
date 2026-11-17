package org.demo.minicore.config;

import org.apache.coyote.AbstractProtocol;
import org.springframework.boot.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public TomcatProtocolHandlerCustomizer<?> tomcatThreadNameCustomizer() {

        return protocolHandler -> {

            if (protocolHandler instanceof AbstractProtocol<?> protocol) {

                protocol.setProperty("name","CheckIn");
            }
        };
    }
}