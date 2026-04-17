package com.hotdeal.platform.ingestion.core;

import com.hotdeal.platform.ingestion.persistence.entity.CollectorJobExecutionEntity;
import com.hotdeal.platform.ingestion.persistence.entity.JobExecutionStatus;
import com.hotdeal.platform.ingestion.persistence.entity.JobTriggerType;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import com.hotdeal.platform.ingestion.persistence.repository.CollectorJobExecutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

@Service
public class CollectorJobExecutionService {

    public static final String COLLECT_DEALS_JOB_NAME = "collect_deals_job";

    private final CollectorJobExecutionRepository collectorJobExecutionRepository;
    private final Clock clock;

    public CollectorJobExecutionService(CollectorJobExecutionRepository collectorJobExecutionRepository,
                                        Clock clock) {
        this.collectorJobExecutionRepository = collectorJobExecutionRepository;
        this.clock = clock;
    }

    @Transactional
    public CollectorJobExecutionEntity start(SourceEntity source, JobTriggerType triggerType) {
        CollectorJobExecutionEntity execution = new CollectorJobExecutionEntity();
        execution.setSource(source);
        execution.setJobName(COLLECT_DEALS_JOB_NAME);
        execution.setTriggerType(triggerType);
        execution.setStatus(JobExecutionStatus.RUNNING);
        execution.setRunKey(buildRunKey(source.getCode()));
        execution.setStartedAt(now());
        execution.setTotalFetched(0);
        execution.setTotalPersisted(0);
        execution.setTotalFailed(0);
        return collectorJobExecutionRepository.save(execution);
    }

    @Transactional
    public CollectorJobExecutionEntity completeSuccess(CollectorJobExecutionEntity execution,
                                                       RawPersistenceResult rawPersistenceResult,
                                                       int attemptsUsed) {
        execution.setFinishedAt(now());
        execution.setDurationMs(execution.getFinishedAt().toInstant().toEpochMilli() - execution.getStartedAt().toInstant().toEpochMilli());
        execution.setTotalFetched(rawPersistenceResult.totalReceived());
        execution.setTotalPersisted(rawPersistenceResult.inserted() + rawPersistenceResult.updated());
        execution.setTotalFailed(rawPersistenceResult.failed());
        execution.setStatus(rawPersistenceResult.failed() > 0 ? JobExecutionStatus.PARTIAL : JobExecutionStatus.SUCCESS);
        execution.setDetails(Map.of(
                "attemptsUsed", attemptsUsed,
                "inserted", rawPersistenceResult.inserted(),
                "updated", rawPersistenceResult.updated(),
                "failed", rawPersistenceResult.failed()
        ));
        execution.setErrorMessage(null);
        return collectorJobExecutionRepository.save(execution);
    }

    @Transactional
    public CollectorJobExecutionEntity completeFailure(CollectorJobExecutionEntity execution,
                                                       int attemptsUsed,
                                                       String errorMessage) {
        execution.setFinishedAt(now());
        execution.setDurationMs(execution.getFinishedAt().toInstant().toEpochMilli() - execution.getStartedAt().toInstant().toEpochMilli());
        execution.setStatus(JobExecutionStatus.FAILED);
        execution.setErrorMessage(errorMessage);
        execution.setDetails(Map.of(
                "attemptsUsed", attemptsUsed
        ));
        return collectorJobExecutionRepository.save(execution);
    }

    private String buildRunKey(String sourceCode) {
        return sourceCode + "-" + now().toInstant().toEpochMilli() + "-" + UUID.randomUUID();
    }

    private OffsetDateTime now() {
        return OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }
}
