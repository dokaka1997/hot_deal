package com.hotdeal.platform.common.web;

import com.hotdeal.platform.common.api.ApiResponse;
import com.hotdeal.platform.common.api.ApiResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseApiController {

    private final ApiResponseFactory apiResponseFactory;

    protected BaseApiController(ApiResponseFactory apiResponseFactory) {
        this.apiResponseFactory = apiResponseFactory;
    }

    protected <T> ResponseEntity<ApiResponse<T>> ok(HttpServletRequest request, T data) {
        return ResponseEntity.ok(apiResponseFactory.success(request, data));
    }

    protected ResponseEntity<ApiResponse<Void>> created(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponseFactory.success(request));
    }

    protected ResponseEntity<ApiResponse<Void>> accepted(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(apiResponseFactory.success(request));
    }
}
