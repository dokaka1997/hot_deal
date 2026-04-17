package com.hotdeal.platform.ingestion.collector;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;

public record CollectorContext(
        Long sourceId,
        String sourceCode,
        String sourceName,
        String baseUrl,
        Map<String, Object> sourceMetadata,
        int attempt,
        int maxAttempts,
        OffsetDateTime triggeredAt
) {
    public CollectorContext {
        sourceMetadata = sourceMetadata == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(sourceMetadata));
    }
}
