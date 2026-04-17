package com.hotdeal.platform.ingestion.core;

import com.hotdeal.platform.ingestion.collector.CollectedRawDeal;
import com.hotdeal.platform.ingestion.collector.CollectorContext;
import com.hotdeal.platform.ingestion.collector.DealCollector;
import com.hotdeal.platform.ingestion.config.IngestionProperties;
import com.hotdeal.platform.ingestion.persistence.entity.CollectorJobExecutionEntity;
import com.hotdeal.platform.ingestion.persistence.entity.JobTriggerType;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import com.hotdeal.platform.ingestion.persistence.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class CollectorOrchestrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorOrchestrationService.class);

    private final DealCollectorRegistry dealCollectorRegistry;
    private final CollectorJobExecutionService collectorJobExecutionService;
    private final RawDealPersistenceService rawDealPersistenceService;
    private final SourceRepository sourceRepository;
    private final IngestionProperties ingestionProperties;
    private final Clock clock;

    public CollectorOrchestrationService(DealCollectorRegistry dealCollectorRegistry,
                                         CollectorJobExecutionService collectorJobExecutionService,
                                         RawDealPersistenceService rawDealPersistenceService,
                                         SourceRepository sourceRepository,
                                         IngestionProperties ingestionProperties,
                                         Clock clock) {
        this.dealCollectorRegistry = dealCollectorRegistry;
        this.collectorJobExecutionService = collectorJobExecutionService;
        this.rawDealPersistenceService = rawDealPersistenceService;
        this.sourceRepository = sourceRepository;
        this.ingestionProperties = ingestionProperties;
        this.clock = clock;
    }

    public CollectorRunResult runBySourceCode(String sourceCode, JobTriggerType triggerType) {
        SourceEntity source = sourceRepository.findByCode(sourceCode)
                .orElseThrow(() -> new IllegalStateException("Source not found: " + sourceCode));
        return run(source, triggerType);
    }

    public CollectorRunResult run(SourceEntity source, JobTriggerType triggerType) {
        CollectorJobExecutionEntity execution = collectorJobExecutionService.start(source, triggerType);
        int attemptsUsed = 0;

        try {
            DealCollector collector = dealCollectorRegistry.resolve(source.getCode())
                    .orElseThrow(() -> new IllegalStateException("No collector registered for source code: " + source.getCode()));

            CollectAttempt collectAttempt = collectWithRetry(source, collector);
            attemptsUsed = collectAttempt.attemptsUsed();
            RawPersistenceResult persistenceResult = rawDealPersistenceService.persist(source, execution, collectAttempt.records());

            collectorJobExecutionService.completeSuccess(execution, persistenceResult, attemptsUsed);
            markSourceSuccess(source);

            LOGGER.info("collector run success sourceCode={} attempts={} received={} inserted={} updated={} failed={}",
                    source.getCode(),
                    attemptsUsed,
                    persistenceResult.totalReceived(),
                    persistenceResult.inserted(),
                    persistenceResult.updated(),
                    persistenceResult.failed());

            return new CollectorRunResult(
                    source.getCode(),
                    attemptsUsed,
                    persistenceResult,
                    true,
                    null
            );
        } catch (Exception exception) {
            String errorMessage = buildSafeMessage(exception);
            collectorJobExecutionService.completeFailure(execution, attemptsUsed, errorMessage);
            markSourceFailure(source, errorMessage);

            LOGGER.error("collector run failed sourceCode={} attempts={} message={}",
                    source.getCode(), attemptsUsed, errorMessage, exception);

            return new CollectorRunResult(
                    source.getCode(),
                    attemptsUsed,
                    new RawPersistenceResult(0, 0, 0, 0),
                    false,
                    errorMessage
            );
        }
    }

    private CollectAttempt collectWithRetry(SourceEntity source, DealCollector collector) {
        int maxAttempts = Math.max(1, ingestionProperties.getRetry().getMaxAttempts());
        long backoffMs = Math.max(0L, ingestionProperties.getRetry().getBackoffMs());
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                CollectorContext context = new CollectorContext(
                        source.getId(),
                        source.getCode(),
                        source.getName(),
                        source.getBaseUrl(),
                        source.getMetadata(),
                        attempt,
                        maxAttempts,
                        now()
                );
                List<CollectedRawDeal> records = collector.collect(context);
                return new CollectAttempt(records, attempt);
            } catch (Exception exception) {
                lastException = exception;
                LOGGER.warn("collector attempt failed sourceCode={} collector={} attempt={}/{} message={}",
                        source.getCode(), collector.collectorName(), attempt, maxAttempts, exception.getMessage());
                if (attempt < maxAttempts && backoffMs > 0) {
                    sleep(backoffMs * attempt);
                }
            }
        }

        throw new IllegalStateException("Collector failed after retries for source code: " + source.getCode(), lastException);
    }

    protected void markSourceSuccess(SourceEntity source) {
        source.setLastSuccessAt(now());
        source.setLastErrorMessage(null);
        sourceRepository.save(source);
    }

    protected void markSourceFailure(SourceEntity source, String errorMessage) {
        source.setLastFailureAt(now());
        source.setLastErrorMessage(errorMessage);
        sourceRepository.save(source);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Collector retry backoff interrupted", interruptedException);
        }
    }

    private OffsetDateTime now() {
        return OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }

    private String buildSafeMessage(Exception exception) {
        return StringUtils.hasText(exception.getMessage()) ? exception.getMessage() : exception.getClass().getSimpleName();
    }

    private record CollectAttempt(List<CollectedRawDeal> records, int attemptsUsed) {
    }
}
