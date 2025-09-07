package com.example.SampleProject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecordNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    private final String errorCode;

    public RecordNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
        this.errorCode = "RECORD_NOT_FOUND";
    }

    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
        this.errorCode = "RECORD_NOT_FOUND";
    }

    public RecordNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errorCode = "RECORD_NOT_FOUND";
    }

    public RecordNotFoundException(String resourceName, String fieldName, Object fieldValue, String errorCode) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errorCode = errorCode;
    }

    // Getters
    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public String getErrorCode() {
        return errorCode;
    }
}