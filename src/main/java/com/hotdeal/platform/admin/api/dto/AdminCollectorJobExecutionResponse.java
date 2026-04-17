package com.hotdeal.platform.admin.api.dto;

import com.hotdeal.platform.ingestion.persistence.entity.JobExecutionStatus;
import com.hotdeal.platform.ingestion.persistence.entity.JobTriggerType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(name = "AdminCollectorJobExecutionResponse", description = "Collector job execution record for operations.")
public record AdminCollectorJobExecutionResponse(
        Long id,
        String sourceCode,
        String sourceName,
        String jobName,
        JobTriggerType triggerType,
        JobExecutionStatus status,
        String runKey,
        OffsetDateTime startedAt,
        OffsetDateTime finishedAt,
        Long durationMs,
        Integer totalFetched,
        Integer totalPersisted,
        Integer totalFailed,
        String errorMessage
) {
}
