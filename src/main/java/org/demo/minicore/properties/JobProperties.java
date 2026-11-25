package org.demo.minicore.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch.processing.fetch")
public class JobProperties {

    private int jobSize;

    public int getJobSize() {
        return jobSize;
    }

    public void setJobSize(int jobSize) {
        this.jobSize = jobSize;
    }

}
