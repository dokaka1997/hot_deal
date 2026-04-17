package com.hotdeal.platform.deal.scoring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.scoring")
public class DealScoringProperties {

    private int lookbackDays = 30;
    private double defaultSourceConfidence = 0.6d;
    private final Snapshot snapshot = new Snapshot();
    private final Scheduler scheduler = new Scheduler();

    public int getLookbackDays() {
        return lookbackDays;
    }

    public void setLookbackDays(int lookbackDays) {
        this.lookbackDays = lookbackDays;
    }

    public double getDefaultSourceConfidence() {
        return defaultSourceConfidence;
    }

    public void setDefaultSourceConfidence(double defaultSourceConfidence) {
        this.defaultSourceConfidence = defaultSourceConfidence;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public static class Snapshot {
        private long minIntervalMinutes = 60L;

        public long getMinIntervalMinutes() {
            return minIntervalMinutes;
        }

        public void setMinIntervalMinutes(long minIntervalMinutes) {
            this.minIntervalMinutes = minIntervalMinutes;
        }
    }

    public static class Scheduler {
        private boolean enabled = true;
        private String cron = "15 */10 * * * *";
        private int batchSize = 200;

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

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }
    }
}
