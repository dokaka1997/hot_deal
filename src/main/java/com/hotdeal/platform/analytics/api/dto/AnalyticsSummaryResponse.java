package com.hotdeal.platform.analytics.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record AnalyticsSummaryResponse(
        OffsetDateTime generatedAt,
        boolean activeOnly,
        long activeDealCount,
        long expiredDealCount,
        BigDecimal averageDiscountPercent,
        List<AnalyticsDealCountBySourceResponse> dealCountBySource,
        List<AnalyticsDealCountByCategoryResponse> dealCountByCategory,
        List<AnalyticsHottestDealResponse> hottestDeals
) {
}
