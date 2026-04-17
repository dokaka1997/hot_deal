package com.hotdeal.platform.ingestion.normalization;

import com.hotdeal.platform.ingestion.persistence.entity.RawDealStatus;

public record NormalizationSingleResult(
        Long rawDealId,
        RawDealStatus status,
        String message
) {
}
