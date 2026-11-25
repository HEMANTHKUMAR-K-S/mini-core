package org.demo.minicore.config;


import org.demo.minicore.properties.RetryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate(RetryProperties retryProperties) {


        Map<Class<? extends Throwable>, RetryPolicy> retryPolicies = new HashMap<>();

        Map<Class<? extends Throwable>, BackOffPolicy> backOffPolicies = new HashMap<>();

        for (RetryProperties.exceotionConfig config : retryProperties.getExceptions()) {
            try {
            Class<?> loadclass = Class.forName(config.getName());


            Class<? extends Throwable> exceptionClass = loadclass.asSubclass(Throwable.class);

                if (config.getMaxAttempts() < 1 || config.getDelay() < 0) {

                    throw new IllegalArgumentException(" Illegal Exception Occurs " + config.getName());
                }

                SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(config.getMaxAttempts());

                retryPolicies.put(exceptionClass, retryPolicy);

                FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();

                backOffPolicy.setBackOffPeriod(config.getDelay());

                backOffPolicies.put(exceptionClass, backOffPolicy);

            } catch (ClassNotFoundException | ClassCastException e) {
                throw new IllegalStateException(" Invalid Exception retry : " + config.getName(),e);
            }


        }


        ExceptionClassifierRetryPolicy exceptionRetryPolicy =  new ExceptionClassifierRetryPolicy();

        exceptionRetryPolicy.setPolicyMap(retryPolicies);

        RetryTemplate retryTemplate=new RetryTemplate();

        retryTemplate.setRetryPolicy(exceptionRetryPolicy);

        retryTemplate.setBackOffPolicy(new CustomExceptionBackOffPolicy(retryProperties));

        return retryTemplate;
    }
}
