package com.hotdeal.platform.common.api;

public record ApiFieldError(
        String field,
        String message,
        String rejectedValue
) {
}
