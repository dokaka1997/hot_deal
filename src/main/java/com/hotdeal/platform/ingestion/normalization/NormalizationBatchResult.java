package com.hotdeal.platform.ingestion.normalization;

public record NormalizationBatchResult(
        int selected,
        int normalized,
        int rejected,
        int failed
) {
}
