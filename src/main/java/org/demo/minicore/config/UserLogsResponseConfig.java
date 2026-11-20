//package org.demo.minicore.config;
//
//
//import jakarta.persistence.criteria.CriteriaBuilder;
//import org.demo.minicore.dto.UserLogsResoponse;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.integration.dsl.IntegrationFlow;
//import org.springframework.integration.http.dsl.Http;
//
//@Configuration
//public class UserLogsResponseConfig {
//
//    @Bean
//    public IntegrationFlow userResponseFlow(){
//        return IntegrationFlow
//                .from(
//                        Http.inboundGateway("/userlog-response")
//                                .requestMapping(requestMappingSpec ->
//                                        requestMappingSpec.methods(HttpMethod.POST))
//                )
//                .handle(UserLogsResoponse.class,(response,header)-> response)
//                .get();
//    }
//
//}
