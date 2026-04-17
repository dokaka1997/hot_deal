package com.hotdeal.platform.alert.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.alert")
public class AlertProperties {

    private final Matching matching = new Matching();

    public Matching getMatching() {
        return matching;
    }

    public static class Matching {
        private int bootstrapScanSize = 200;

        public int getBootstrapScanSize() {
            return bootstrapScanSize;
        }

        public void setBootstrapScanSize(int bootstrapScanSize) {
            this.bootstrapScanSize = bootstrapScanSize;
        }
    }
}
