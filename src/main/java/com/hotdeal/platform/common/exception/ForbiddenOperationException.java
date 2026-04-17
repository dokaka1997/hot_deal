package com.hotdeal.platform.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenOperationException extends ApiException {
    public ForbiddenOperationException(String message) {
        super(ErrorCode.OPERATION_FORBIDDEN, message, HttpStatus.FORBIDDEN);
    }
}
