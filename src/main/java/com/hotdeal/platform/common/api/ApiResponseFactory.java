package com.hotdeal.platform.common.api;

import com.hotdeal.platform.common.logging.LogContext;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class ApiResponseFactory {

    public <T> ApiResponse<T> success(HttpServletRequest request, T data) {
        return ApiResponse.success(request.getRequestURI(), resolveTraceId(), data);
    }

    public ApiResponse<Void> success(HttpServletRequest request) {
        return ApiResponse.success(request.getRequestURI(), resolveTraceId(), null);
    }

    public ApiResponse<Void> error(HttpServletRequest request, ApiError apiError) {
        return ApiResponse.error(request.getRequestURI(), resolveTraceId(), apiError);
    }

    private String resolveTraceId() {
        return MDC.get(LogContext.TRACE_ID);
    }
}
