package com.hotdeal.platform.admin.api.dto;

import com.hotdeal.platform.ingestion.persistence.entity.SourceStatus;
import com.hotdeal.platform.ingestion.persistence.entity.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(name = "AdminSourceHealthResponse", description = "Operational health view of a source.")
public record AdminSourceHealthResponse(
        Long sourceId,
        String sourceCode,
        String sourceName,
        SourceType sourceType,
        SourceStatus sourceStatus,
        OffsetDateTime lastSuccessAt,
        OffsetDateTime lastFailureAt,
        String lastErrorMessage,
        String lastJobStatus,
        OffsetDateTime lastJobStartedAt,
        OffsetDateTime lastJobFinishedAt,
        long jobsLast24Hours,
        long failuresLast24Hours
) {
}
