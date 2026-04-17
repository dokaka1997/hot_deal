package com.hotdeal.platform.common.api;

import java.time.Instant;

public record ApiResponse<T>(
        Instant timestamp,
        boolean success,
        String path,
        String traceId,
        T data,
        ApiError error
) {
    public static <T> ApiResponse<T> success(String path, String traceId, T data) {
        return new ApiResponse<>(Instant.now(), true, path, traceId, data, null);
    }

    public static ApiResponse<Void> error(String path, String traceId, ApiError error) {
        return new ApiResponse<>(Instant.now(), false, path, traceId, null, error);
    }
}
