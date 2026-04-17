package com.hotdeal.platform.analytics.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AnalyticsHottestDealResponse(
        Long dealId,
        String title,
        String brand,
        String category,
        String sourceCode,
        String sourceName,
        BigDecimal dealPrice,
        BigDecimal originalPrice,
        BigDecimal discountPercent,
        BigDecimal dealScore,
        OffsetDateTime lastSeenAt,
        String externalUrl,
        String imageUrl
) {
}
