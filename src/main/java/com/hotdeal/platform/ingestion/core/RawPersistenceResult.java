package com.hotdeal.platform.ingestion.core;

public record RawPersistenceResult(
        int totalReceived,
        int inserted,
        int updated,
        int failed
) {
}
