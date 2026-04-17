package com.hotdeal.platform.ingestion.normalization.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.normalization")
public class NormalizationProperties {

    private int batchSize = 100;
    private final Scheduler scheduler = new Scheduler();

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public static class Scheduler {
        private boolean enabled = true;
        private String cron = "30 */5 * * * *";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }
}
