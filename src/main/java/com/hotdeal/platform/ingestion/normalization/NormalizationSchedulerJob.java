package com.hotdeal.platform.ingestion.normalization;

import com.hotdeal.platform.ingestion.normalization.config.NormalizationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class NormalizationSchedulerJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(NormalizationSchedulerJob.class);

    private final DealNormalizationService dealNormalizationService;
    private final NormalizationProperties normalizationProperties;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public NormalizationSchedulerJob(DealNormalizationService dealNormalizationService,
                                     NormalizationProperties normalizationProperties) {
        this.dealNormalizationService = dealNormalizationService;
        this.normalizationProperties = normalizationProperties;
    }

    @Scheduled(cron = "${app.normalization.scheduler.cron:30 */5 * * * *}")
    public void runNormalization() {
        if (!normalizationProperties.getScheduler().isEnabled()) {
            return;
        }
        if (!running.compareAndSet(false, true)) {
            LOGGER.warn("normalization scheduler skipped because previous run is still active");
            return;
        }
        try {
            NormalizationBatchResult result = dealNormalizationService.normalizePending(normalizationProperties.getBatchSize());
            LOGGER.info("normalization scheduler run selected={} normalized={} rejected={} failed={}",
                    result.selected(), result.normalized(), result.rejected(), result.failed());
        } finally {
            running.set(false);
        }
    }
}
