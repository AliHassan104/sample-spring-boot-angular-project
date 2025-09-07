// ServiceException.java
package com.example.SampleProject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServiceException extends RuntimeException {

    private final String errorCode;

    public ServiceException(String message) {
        super(message);
        this.errorCode = "SERVICE_ERROR";
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SERVICE_ERROR";
    }

    public ServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}