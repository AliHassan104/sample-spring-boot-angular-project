package com.example.SampleProject.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Custom business exceptions
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleRecordNotFoundException(
            RecordNotFoundException ex, HttpServletRequest request) {

        log.warn("Record not found: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .details("The requested resource was not found")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .errorCode(ex.getErrorCode())
                .timestamp(LocalDateTime.now())
                .build();

        if (ex.getResourceName() != null) {
            Map<String, Object> additionalInfo = new HashMap<>();
            additionalInfo.put("resourceName", ex.getResourceName());
            additionalInfo.put("fieldName", ex.getFieldName());
            additionalInfo.put("fieldValue", ex.getFieldValue());
            errorMessage.setAdditionalInfo(additionalInfo);
        }

        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorMessage> handleDuplicateResourceException(
            DuplicateResourceException ex, HttpServletRequest request) {

        log.warn("Duplicate resource: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .details("A resource with the same identifier already exists")
                .status(HttpStatus.CONFLICT.value())
                .path(request.getRequestURI())
                .errorCode(ex.getErrorCode())
                .timestamp(LocalDateTime.now())
                .build();

        if (ex.getResourceName() != null) {
            Map<String, Object> additionalInfo = new HashMap<>();
            additionalInfo.put("resourceName", ex.getResourceName());
            additionalInfo.put("fieldName", ex.getFieldName());
            additionalInfo.put("fieldValue", ex.getFieldValue());
            errorMessage.setAdditionalInfo(additionalInfo);
        }

        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorMessage> handleServiceException(
            ServiceException ex, HttpServletRequest request) {

        log.error("Service error: {}", ex.getMessage(), ex);

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .details("An internal service error occurred")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .errorCode(ex.getErrorCode())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(
            ValidationException ex, HttpServletRequest request) {

        log.warn("Validation error: {}", ex.getMessage());

        List<ErrorMessage.FieldError> fieldErrors = new ArrayList<>();
        if (ex.getFieldErrors() != null) {
            fieldErrors = ex.getFieldErrors().entrySet().stream()
                    .map(entry -> ErrorMessage.FieldError.builder()
                            .field(entry.getKey())
                            .message(entry.getValue())
                            .code("INVALID")
                            .build())
                    .collect(Collectors.toList());
        }

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .details("Request validation failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errorCode(ex.getErrorCode())
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // Validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.warn("Validation failed: {}", ex.getMessage());

        List<ErrorMessage.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> ErrorMessage.FieldError.builder()
                        .field(error.getField())
                        .rejectedValue(error.getRejectedValue())
                        .message(error.getDefaultMessage())
                        .code(error.getCode())
                        .build())
                .collect(Collectors.toList());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("Request validation failed")
                .details("One or more fields have invalid values")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errorCode("VALIDATION_ERROR")
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {

        log.warn("Constraint violation: {}", ex.getMessage());

        List<ErrorMessage.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(violation -> ErrorMessage.FieldError.builder()
                        .field(getFieldName(violation))
                        .rejectedValue(violation.getInvalidValue())
                        .message(violation.getMessage())
                        .code("CONSTRAINT_VIOLATION")
                        .build())
                .collect(Collectors.toList());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("Constraint validation failed")
                .details("One or more constraints were violated")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errorCode("CONSTRAINT_VIOLATION")
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // Security exceptions
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessage> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {

        log.warn("Authentication failed: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("Authentication failed")
                .details("Invalid username or password")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .errorCode("AUTHENTICATION_FAILED")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("Access denied: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("Access denied")
                .details("You don't have permission to access this resource")
                .status(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .errorCode("ACCESS_DENIED")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessage> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        log.warn("Authentication error: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("Authentication required")
                .details("Valid authentication credentials are required")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .errorCode("AUTHENTICATION_REQUIRED")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    // JWT exceptions
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorMessage> handleExpiredJwtException(
            ExpiredJwtException ex, HttpServletRequest request) {

        log.warn("JWT token expired: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("JWT token has expired")
                .details("Please login again to get a new token")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .errorCode("JWT_EXPIRED")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({MalformedJwtException.class, UnsupportedJwtException.class})
    public ResponseEntity<ErrorMessage> handleJwtException(
            RuntimeException ex, HttpServletRequest request) {

        log.warn("Invalid JWT token: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("Invalid JWT token")
                .details("The provided token is malformed or unsupported")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .errorCode("JWT_INVALID")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    // HTTP exceptions
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessage> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        log.warn("Method not supported: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("HTTP method not supported")
                .details(String.format("Method '%s' is not supported for this endpoint", ex.getMethod()))
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .path(request.getRequestURI())
                .errorCode("METHOD_NOT_ALLOWED")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorMessage> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {

        log.warn("No handler found: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("Endpoint not found")
                .details(String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()))
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .errorCode("ENDPOINT_NOT_FOUND")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        log.warn("Malformed JSON request: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("Malformed JSON request")
                .details("The request body contains invalid JSON")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errorCode("MALFORMED_JSON")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        log.warn("Argument type mismatch: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("Invalid parameter type")
                .details(String.format("Parameter '%s' should be of type %s",
                        ex.getName(), ex.getRequiredType().getSimpleName()))
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errorCode("INVALID_PARAMETER_TYPE")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessage> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        log.warn("Missing request parameter: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("Missing required parameter")
                .details(String.format("Required parameter '%s' is missing", ex.getParameterName()))
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errorCode("MISSING_PARAMETER")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // Database exceptions
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        log.error("Data integrity violation: {}", ex.getMessage(), ex);

        String message = "Data integrity violation";
        String details = "The operation violates database constraints";

        if (ex.getMessage().contains("Duplicate entry")) {
            message = "Duplicate entry";
            details = "A record with the same unique field already exists";
        } else if (ex.getMessage().contains("foreign key constraint")) {
            message = "Foreign key constraint violation";
            details = "Cannot delete/update due to existing related records";
        }

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(message)
                .details(details)
                .status(HttpStatus.CONFLICT.value())
                .path(request.getRequestURI())
                .errorCode("DATA_INTEGRITY_VIOLATION")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    // Illegal argument exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .details("Invalid argument provided")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errorCode("ILLEGAL_ARGUMENT")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message("An unexpected error occurred")
                .details("Please contact support if this problem persists")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .errorCode("INTERNAL_ERROR")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper method to extract field name from constraint violation
    private String getFieldName(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        return propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
    }
}