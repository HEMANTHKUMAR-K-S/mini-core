//package org.demo.minicore.config;
//
//
//import org.demo.minicore.dto.UserLogsResoponse;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.dsl.IntegrationFlow;
//import org.springframework.integration.http.dsl.Http;
//import org.springframework.messaging.MessageChannel;
//
//@Configuration
//public class OutBoundConfig {
//
//    @Bean
//    public MessageChannel CheckOutResponseChannel(){
//
//        return new DirectChannel();
//    }
//
//    @Bean
//    public IntegrationFlow checkOutResponseFlow(){
//
//        return IntegrationFlow
//                .from("CheckOutResponseChannel")
//                .handle(
//                        Http.outboundGateway("""
//                                        http://localhost:8080/userlog-response""")
//                                .httpMethod(HttpMethod.POST)
//                                .mappedRequestHeaders("Content-Type")
//                                .expectedResponseType(UserLogsResoponse.class)
//                )
//                .get();
//
//    }
//
//}
