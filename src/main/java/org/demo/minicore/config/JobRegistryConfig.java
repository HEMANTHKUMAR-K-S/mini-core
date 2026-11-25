
package org.demo.minicore.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobRegistryConfig {

    @Bean
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }
}
