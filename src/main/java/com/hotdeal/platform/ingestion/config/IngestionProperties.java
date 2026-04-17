package com.hotdeal.platform.ingestion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ingestion")
public class IngestionProperties {

    private boolean bootstrapSampleSource = true;
    private final Scheduler scheduler = new Scheduler();
    private final Retry retry = new Retry();

    public boolean isBootstrapSampleSource() {
        return bootstrapSampleSource;
    }

    public void setBootstrapSampleSource(boolean bootstrapSampleSource) {
        this.bootstrapSampleSource = bootstrapSampleSource;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Retry getRetry() {
        return retry;
    }

    public static class Scheduler {
        private boolean enabled = true;
        private String cron = "0 */5 * * * *";

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

    public static class Retry {
        private int maxAttempts = 2;
        private long backoffMs = 500;

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public long getBackoffMs() {
            return backoffMs;
        }

        public void setBackoffMs(long backoffMs) {
            this.backoffMs = backoffMs;
        }
    }
}
