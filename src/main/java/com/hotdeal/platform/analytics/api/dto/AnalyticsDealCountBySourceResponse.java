package com.hotdeal.platform.analytics.api.dto;

public record AnalyticsDealCountBySourceResponse(
        String sourceCode,
        String sourceName,
        long dealCount
) {
}
