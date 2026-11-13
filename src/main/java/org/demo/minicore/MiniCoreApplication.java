package org.demo.minicore;

import org.demo.minicore.rmq.rmqServices;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

@PropertySource(
        "file:C:/Users/Solum_BLR_IND/Desktop/intervals/application.properties"
)
public class MiniCoreApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MiniCoreApplication.class, args);
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
         return application.sources(MiniCoreApplication.class);
    }

    @Bean
    CommandLineRunner initializeRabbitMQ(rmqServices rmqServices) {
        return args -> rmqServices.createExchange();
    }
}
