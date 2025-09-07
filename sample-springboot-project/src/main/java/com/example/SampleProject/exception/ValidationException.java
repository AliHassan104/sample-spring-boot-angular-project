// ValidationException.java
package com.example.SampleProject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

    private final Map<String, String> fieldErrors;
    private final String errorCode;

    public ValidationException(String message) {
        super(message);
        this.fieldErrors = null;
        this.errorCode = "VALIDATION_ERROR";
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
        this.errorCode = "VALIDATION_ERROR";
    }

    public ValidationException(String message, Map<String, String> fieldErrors, String errorCode) {
        super(message);
        this.fieldErrors = fieldErrors;
        this.errorCode = errorCode;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public String getErrorCode() {
        return errorCode;
    }
}