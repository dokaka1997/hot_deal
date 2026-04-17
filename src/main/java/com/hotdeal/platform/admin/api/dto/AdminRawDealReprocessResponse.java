package com.hotdeal.platform.admin.api.dto;

import com.hotdeal.platform.ingestion.persistence.entity.RawDealStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AdminRawDealReprocessResponse", description = "Outcome of manual reprocess for one raw deal.")
public record AdminRawDealReprocessResponse(
        Long rawDealId,
        RawDealStatus status,
        String message
) {
}
