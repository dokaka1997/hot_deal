package com.hotdeal.platform.ingestion.collector;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;

public record CollectedRawDeal(
        String sourceDealId,
        String sourceRecordKey,
        String sourceRecordHash,
        Map<String, Object> payload,
        String payloadVersion
) {
    public CollectedRawDeal {
        payload = payload == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(payload));
    }
}
