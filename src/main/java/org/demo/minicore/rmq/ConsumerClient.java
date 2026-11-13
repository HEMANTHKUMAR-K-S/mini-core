package org.demo.minicore.rmq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ConsumerClient {

    private final RestClient restClient;

    public ConsumerClient(
            RestClient.Builder builder,
            @Value("${consumer.base-url}") String consumerUrl) {
        this.restClient = builder
                .baseUrl(consumerUrl)
                .build();
    }
    public String startConsumer(String username) {

        return restClient.post()
                .uri("/consumer/start/{username}", username)
                .retrieve()
                .body(String.class);
    }

    public String stopConsumer(String username) {

        return restClient.post()
                .uri("/consumer/stop/{username}", username)
                .retrieve()
                .body(String.class);
    }
}