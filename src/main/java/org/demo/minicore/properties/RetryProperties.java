package org.demo.minicore.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "batch.processing.retry")
public class RetryProperties {


    List<exceotionConfig> exceptions=new ArrayList<>();

    public List<exceotionConfig> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<exceotionConfig> exceptions) {
        this.exceptions = exceptions;
    }

    public static class exceotionConfig{

        private String name;
        private int maxAttempts;
        private long delay;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public long getDelay() {
            return delay;
        }

        public void setDelay(long delay) {
            this.delay = delay;
        }
    }
}
