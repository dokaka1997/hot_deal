package com.hotdeal.platform.ingestion.core;

public record CollectorRunResult(
        String sourceCode,
        int attemptsUsed,
        RawPersistenceResult persistenceResult,
        boolean success,
        String errorMessage
) {
}
