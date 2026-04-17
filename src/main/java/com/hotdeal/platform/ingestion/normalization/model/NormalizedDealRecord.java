package com.hotdeal.platform.ingestion.normalization.model;

import com.hotdeal.platform.deal.persistence.entity.DealStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

public record NormalizedDealRecord(
        String sourceDealId,
        String title,
        String normalizedTitle,
        String description,
        String brand,
        String category,
        String externalUrl,
        String imageUrl,
        String currency,
        BigDecimal originalPrice,
        BigDecimal dealPrice,
        String couponCode,
        OffsetDateTime validFrom,
        OffsetDateTime validUntil,
        DealStatus status,
        Map<String, Object> metadata
) {
}
