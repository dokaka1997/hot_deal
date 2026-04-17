package com.hotdeal.platform.admin.api.dto;

import com.hotdeal.platform.ingestion.persistence.entity.RawDealStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(name = "AdminFailedRawDealResponse", description = "Failed raw deal record for troubleshooting and reprocess.")
public record AdminFailedRawDealResponse(
        Long rawDealId,
        Long sourceId,
        String sourceCode,
        String sourceName,
        Long jobExecutionId,
        String sourceDealId,
        String sourceRecordKey,
        String sourceRecordHash,
        RawDealStatus status,
        OffsetDateTime ingestedAt,
        OffsetDateTime normalizedAt,
        String parseError
) {
}
