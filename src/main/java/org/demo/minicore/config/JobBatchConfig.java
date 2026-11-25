package org.demo.minicore.config;


import org.demo.minicore.properties.JobProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JobProperties.class)
public class JobBatchConfig {

}
