package me.findthepeach.common.response.handler;

import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.model.ApiResponse;
import me.findthepeach.common.response.exception.*;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {
        log.info("GlobalExceptionHandler initialized");
    }

    // Business Exception
    @ExceptionHandler({
            UserException.class,
            ShopException.class,
            InventoryException.class,
            OrderException.class
    })
    public ResponseEntity<ApiResponse<?>> handleBusinessExceptions(BaseException e) {
        String domain = e.getClass().getSimpleName().replace("Exception", "");
        log.error("Business exception occurred in {} domain: traceId={}, code={}, message={}",
                domain, e.getTraceId(), e.getCode(), e.getMessage(), e);
        return createErrorResponse(e.getCode(), e.getMessage(), e.getHttpStatus());
    }

    // System Exception
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiResponse<?>> handleSystemException(SystemException e) {
        log.error("System exception occurred: traceId={}, code={}, message={}",
                e.getTraceId(), e.getCode(), e.getMessage(), e);
        return createErrorResponse(e.getCode(), e.getMessage(), e.getHttpStatus());
    }

    // Spring Security Exception
    @ExceptionHandler({
            AccessDeniedException.class,
            AuthenticationException.class,
            BadCredentialsException.class,
            InvalidBearerTokenException.class
    })
    public ResponseEntity<ApiResponse<?>> handleAuthenticationExceptions(Exception e) {
        log.error("Authentication error occurred", e);

        ReturnCode returnCode = ReturnCode.UNAUTHORIZED;
        if (e instanceof BadCredentialsException || e instanceof InvalidBearerTokenException) {
            returnCode = ReturnCode.INVALID_TOKEN;
        } else if (e instanceof AccessDeniedException) {
            returnCode = ReturnCode.INSUFFICIENT_PERMISSIONS;
        }

        return createErrorResponse(returnCode);
    }

    // Database Exception
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<?>> handleDatabaseExceptions(DataAccessException e) {
        log.error("Database error occurred", e);
        if (e instanceof QueryTimeoutException) {
            return createErrorResponse(ReturnCode.DATABASE_TIMEOUT);
        } else if (e instanceof DataIntegrityViolationException) {
            return createErrorResponse(ReturnCode.DATA_INTEGRITY_ERROR);
        }
        return createErrorResponse(ReturnCode.DATABASE_ERROR);
    }

    // Parameters Validation Exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("Validation error occurred: {}", message);
        return createErrorResponse(ReturnCode.INVALID_PARAMETER.getCode(), message, HttpStatus.BAD_REQUEST);
    }

    // HTTP request Exception
    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ApiResponse<?>> handleHttpRequestExceptions(Exception e) {
        log.error("HTTP request error occurred", e);
        return createErrorResponse(ReturnCode.INVALID_PARAMETER);
    }

    // Other Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleOtherExceptions(Exception e) {
        log.error("Unhandled exception occurred", e);
        return createErrorResponse(ReturnCode.INTERNAL_ERROR);
    }

    private ResponseEntity<ApiResponse<?>> createErrorResponse(ReturnCode returnCode) {
        return createErrorResponse(returnCode.getCode(), returnCode.getMessage(), returnCode.getHttpStatus());
    }

    private ResponseEntity<ApiResponse<?>> createErrorResponse(int code, String message, HttpStatus httpStatus) {
        return new ResponseEntity<>(ApiResponse.error(code, message), httpStatus);
    }
}