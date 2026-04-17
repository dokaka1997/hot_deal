package com.hotdeal.platform.admin.api.mapper;

import com.hotdeal.platform.admin.api.dto.AdminCollectorJobExecutionResponse;
import com.hotdeal.platform.admin.api.dto.AdminFailedRawDealResponse;
import com.hotdeal.platform.admin.api.dto.AdminSourceHealthResponse;
import com.hotdeal.platform.ingestion.persistence.entity.CollectorJobExecutionEntity;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealEntity;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import org.springframework.stereotype.Component;

@Component
public class AdminMonitoringMapper {

    public AdminCollectorJobExecutionResponse toCollectorJobResponse(CollectorJobExecutionEntity entity) {
        return new AdminCollectorJobExecutionResponse(
                entity.getId(),
                entity.getSource().getCode(),
                entity.getSource().getName(),
                entity.getJobName(),
                entity.getTriggerType(),
                entity.getStatus(),
                entity.getRunKey(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getDurationMs(),
                entity.getTotalFetched(),
                entity.getTotalPersisted(),
                entity.getTotalFailed(),
                entity.getErrorMessage()
        );
    }

    public AdminSourceHealthResponse toSourceHealthResponse(SourceEntity source,
                                                            CollectorJobExecutionEntity lastJob,
                                                            long jobsLast24Hours,
                                                            long failuresLast24Hours) {
        return new AdminSourceHealthResponse(
                source.getId(),
                source.getCode(),
                source.getName(),
                source.getType(),
                source.getStatus(),
                source.getLastSuccessAt(),
                source.getLastFailureAt(),
                source.getLastErrorMessage(),
                lastJob == null ? null : lastJob.getStatus().name(),
                lastJob == null ? null : lastJob.getStartedAt(),
                lastJob == null ? null : lastJob.getFinishedAt(),
                jobsLast24Hours,
                failuresLast24Hours
        );
    }

    public AdminFailedRawDealResponse toFailedRawDealResponse(RawDealEntity entity) {
        Long jobExecutionId = entity.getJobExecution() == null ? null : entity.getJobExecution().getId();
        return new AdminFailedRawDealResponse(
                entity.getId(),
                entity.getSource().getId(),
                entity.getSource().getCode(),
                entity.getSource().getName(),
                jobExecutionId,
                entity.getSourceDealId(),
                entity.getSourceRecordKey(),
                entity.getSourceRecordHash(),
                entity.getStatus(),
                entity.getIngestedAt(),
                entity.getNormalizedAt(),
                entity.getParseError()
        );
    }
}
