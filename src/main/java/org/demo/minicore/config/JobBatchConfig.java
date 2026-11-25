package org.demo.minicore.config;


import org.demo.minicore.properties.JobProperties;
import org.demo.minicore.properties.RetryProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JobProperties.class, RetryProperties.class})
public class JobBatchConfig {
    
}
