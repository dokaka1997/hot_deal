package com.hotdeal.platform.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidInputException extends ApiException {
    public InvalidInputException(String message) {
        super(ErrorCode.INVALID_INPUT, message, HttpStatus.BAD_REQUEST);
    }
}
