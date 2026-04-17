package com.hotdeal.platform.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedOperationException extends ApiException {
    public UnauthorizedOperationException(String message) {
        super(ErrorCode.AUTHENTICATION_REQUIRED, message, HttpStatus.UNAUTHORIZED);
    }
}
