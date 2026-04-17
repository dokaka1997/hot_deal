package com.hotdeal.platform.common.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleViolationException extends ApiException {
    public BusinessRuleViolationException(String message) {
        super(ErrorCode.BUSINESS_RULE_VIOLATION, message, HttpStatus.CONFLICT);
    }
}
