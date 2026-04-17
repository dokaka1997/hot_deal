package com.hotdeal.platform.common.api;

import java.util.List;

public record ApiError(
        String code,
        String message,
        List<ApiFieldError> fieldErrors
) {
    public ApiError {
        fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
    }
}
