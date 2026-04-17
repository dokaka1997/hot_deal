package com.hotdeal.platform.analytics.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Schema(name = "AnalyticsSummaryQueryRequest", description = "Query options for analytics summary APIs.")
public class AnalyticsSummaryQueryRequest {

    @Schema(description = "When true, distribution and hottest deals are limited to active deals only.", defaultValue = "true")
    private boolean activeOnly = true;

    @Min(value = 1, message = "{analytics.query.limit.min}")
    @Max(value = 50, message = "{analytics.query.hottest.max}")
    @Schema(description = "Maximum number of hottest deals returned.", example = "10", defaultValue = "10")
    private int hottestLimit = 10;

    @Min(value = 1, message = "{analytics.query.limit.min}")
    @Max(value = 20, message = "{analytics.query.source.max}")
    @Schema(description = "Maximum number of source buckets returned.", example = "10", defaultValue = "10")
    private int sourceLimit = 10;

    @Min(value = 1, message = "{analytics.query.limit.min}")
    @Max(value = 20, message = "{analytics.query.category.max}")
    @Schema(description = "Maximum number of category buckets returned.", example = "10", defaultValue = "10")
    private int categoryLimit = 10;

    public boolean isActiveOnly() {
        return activeOnly;
    }

    public void setActiveOnly(boolean activeOnly) {
        this.activeOnly = activeOnly;
    }

    public int getHottestLimit() {
        return hottestLimit;
    }

    public void setHottestLimit(int hottestLimit) {
        this.hottestLimit = hottestLimit;
    }

    public int getSourceLimit() {
        return sourceLimit;
    }

    public void setSourceLimit(int sourceLimit) {
        this.sourceLimit = sourceLimit;
    }

    public int getCategoryLimit() {
        return categoryLimit;
    }

    public void setCategoryLimit(int categoryLimit) {
        this.categoryLimit = categoryLimit;
    }
}
