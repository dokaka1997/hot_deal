package com.hotdeal.platform.ingestion.scheduler;

import com.hotdeal.platform.ingestion.config.IngestionProperties;
import com.hotdeal.platform.ingestion.core.CollectorOrchestrationService;
import com.hotdeal.platform.ingestion.persistence.entity.JobTriggerType;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import com.hotdeal.platform.ingestion.persistence.entity.SourceStatus;
import com.hotdeal.platform.ingestion.persistence.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class CollectorSchedulerJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorSchedulerJob.class);

    private final SourceRepository sourceRepository;
    private final CollectorOrchestrationService collectorOrchestrationService;
    private final IngestionProperties ingestionProperties;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public CollectorSchedulerJob(SourceRepository sourceRepository,
                                 CollectorOrchestrationService collectorOrchestrationService,
                                 IngestionProperties ingestionProperties) {
        this.sourceRepository = sourceRepository;
        this.collectorOrchestrationService = collectorOrchestrationService;
        this.ingestionProperties = ingestionProperties;
    }

    @Scheduled(cron = "${app.ingestion.scheduler.cron:0 */5 * * * *}")
    public void runScheduledCollectors() {
        if (!ingestionProperties.getScheduler().isEnabled()) {
            return;
        }

        if (!running.compareAndSet(false, true)) {
            LOGGER.warn("collector scheduler run skipped because previous cycle is still running");
            return;
        }

        try {
            List<SourceEntity> activeSources = sourceRepository.findByStatus(SourceStatus.ACTIVE);
            LOGGER.info("collector scheduler triggered activeSources={}", activeSources.size());
            for (SourceEntity source : activeSources) {
                collectorOrchestrationService.run(source, JobTriggerType.SCHEDULED);
            }
        } finally {
            running.set(false);
        }
    }
}
