package com.hotdeal.platform.analytics.api.dto;

public record AnalyticsDealCountByCategoryResponse(
        String category,
        long dealCount
) {
}
