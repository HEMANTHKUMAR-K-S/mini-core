
package org.demo.minicore.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.messaging.MessageChannel;
import org.springframework.integration.jdbc.inbound.JdbcPollingChannelAdapter;


@Configuration
public class PollerConfig {

    @Bean
    public MessageChannel checkInCountChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel checkOutCountChannel() {
        return new DirectChannel();
    }

    // Check-In DB Poller
    @Bean
    public IntegrationFlow checkInDbPoller(DataSource dataSource) {

        JdbcPollingChannelAdapter adapter =
                new JdbcPollingChannelAdapter(
                        dataSource,
                        "SELECT COUNT(*) AS total " +
                                "FROM user_logs " +
                                "WHERE status = 'CHECK-IN'"
                );

        adapter.setRowMapper(
                (rs, rowNum) -> rs.getLong("total")
        );

        return IntegrationFlow
                .from(adapter,
                        endpoint -> endpoint.poller(
                                Pollers.fixedDelay(100)
                        )
                )
                .channel("checkInCountChannel")
                .get();
    }

    // Check-Out DB Poller
    @Bean
    public IntegrationFlow checkOutDbPoller(DataSource dataSource) {

       JdbcPollingChannelAdapter adapter =
                new JdbcPollingChannelAdapter(
                        dataSource,
                        "SELECT COUNT(*) AS total " +
                                "FROM user_logs " +
                                "WHERE status = 'CHECK-OUT'"
                );

        adapter.setRowMapper(
                (rs, rowNum) -> rs.getLong("total")
        );

        return IntegrationFlow
                .from(adapter,
                        endpoint -> endpoint.poller(
                                Pollers.fixedDelay(10)
                        )
                )
                .channel("checkOutCountChannel")
                .get();
    }
}
