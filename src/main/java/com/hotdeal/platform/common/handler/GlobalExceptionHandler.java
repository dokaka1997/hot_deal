package com.hotdeal.platform.common.handler;

import com.hotdeal.platform.common.api.ApiError;
import com.hotdeal.platform.common.api.ApiFieldError;
import com.hotdeal.platform.common.api.ApiResponse;
import com.hotdeal.platform.common.api.ApiResponseFactory;
import com.hotdeal.platform.common.exception.ApiException;
import com.hotdeal.platform.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;
    private final ApiResponseFactory apiResponseFactory;

    public GlobalExceptionHandler(MessageSource messageSource, ApiResponseFactory apiResponseFactory) {
        this.messageSource = messageSource;
        this.apiResponseFactory = apiResponseFactory;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(ex.getCode(), ex.getMessage(), List.of());
        return ResponseEntity.status(ex.getStatus()).body(apiResponseFactory.error(request, apiError));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                          HttpServletRequest request) {
        List<ApiFieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toApiFieldError)
                .toList();
        String message = resolveMessage("validation.request.invalid");
        ApiError apiError = new ApiError(ErrorCode.VALIDATION_ERROR.name(), message, fieldErrors);
        return ResponseEntity.badRequest().body(apiResponseFactory.error(request, apiError));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException ex,
                                                                 HttpServletRequest request) {
        List<ApiFieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toApiFieldError)
                .toList();
        String message = resolveMessage("validation.parameter.invalid");
        ApiError apiError = new ApiError(ErrorCode.VALIDATION_ERROR.name(), message, fieldErrors);
        return ResponseEntity.badRequest().body(apiResponseFactory.error(request, apiError));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex,
                                                                       HttpServletRequest request) {
        List<ApiFieldError> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(violation -> new ApiFieldError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue() == null ? null : String.valueOf(violation.getInvalidValue())))
                .toList();
        String message = resolveMessage("validation.parameter.invalid");
        ApiError apiError = new ApiError(ErrorCode.CONSTRAINT_VIOLATION.name(), message, fieldErrors);
        return ResponseEntity.badRequest().body(apiResponseFactory.error(request, apiError));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex, HttpServletRequest request) {
        String message = resolveMessage("request.bad");
        ApiError apiError = new ApiError(ErrorCode.BAD_REQUEST.name(), message, List.of());
        return ResponseEntity.badRequest().body(apiResponseFactory.error(request, apiError));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex,
                                                                           HttpServletRequest request) {
        ApiError apiError = new ApiError(ErrorCode.AUTHENTICATION_REQUIRED.name(), ex.getMessage(), List.of());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponseFactory.error(request, apiError));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex,
                                                                         HttpServletRequest request) {
        ApiError apiError = new ApiError(ErrorCode.OPERATION_FORBIDDEN.name(), ex.getMessage(), List.of());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponseFactory.error(request, apiError));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        String message = resolveMessage("error.unexpected");
        LOGGER.error("unexpected error path={}", request.getRequestURI(), ex);
        ApiError apiError = new ApiError(ErrorCode.INTERNAL_ERROR.name(), message, List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiResponseFactory.error(request, apiError));
    }

    private ApiFieldError toApiFieldError(FieldError fieldError) {
        String rejectedValue = fieldError.getRejectedValue() == null ? null : String.valueOf(fieldError.getRejectedValue());
        return new ApiFieldError(fieldError.getField(), fieldError.getDefaultMessage(), rejectedValue);
    }

    private String resolveMessage(String key) {
        try {
            return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException noSuchMessageException) {
            return key;
        }
    }
}
