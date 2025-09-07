package com.example.SampleProject.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "Standard error response structure")
public class ErrorMessage {

    @ApiModelProperty(value = "Success status", example = "false")
    @Builder.Default
    private Boolean success = false;

    @ApiModelProperty(value = "Error message", example = "Validation failed")
    private String message;

    @ApiModelProperty(value = "Detailed error description", example = "The request contains invalid data")
    private String details;

    @ApiModelProperty(value = "HTTP status code", example = "400")
    private Integer status;

    @ApiModelProperty(value = "Request path", example = "/api/classes")
    private String path;

    @ApiModelProperty(value = "Error timestamp", example = "2025-01-20T10:30:00")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @ApiModelProperty(value = "Error code for programmatic handling", example = "VALIDATION_ERROR")
    private String errorCode;

    @ApiModelProperty(value = "Field validation errors")
    private List<FieldError> fieldErrors;

    @ApiModelProperty(value = "Additional error context")
    private Map<String, Object> additionalInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "Field validation error details")
    public static class FieldError {

        @ApiModelProperty(value = "Field name", example = "name")
        private String field;

        @ApiModelProperty(value = "Rejected value", example = "")
        private Object rejectedValue;

        @ApiModelProperty(value = "Error message", example = "Class name is required")
        private String message;

        @ApiModelProperty(value = "Error code", example = "NotBlank")
        private String code;
    }

    // Static factory methods for common error types
    public static ErrorMessage of(String message) {
        return ErrorMessage.builder()
                .message(message)
                .build();
    }

    public static ErrorMessage of(String message, String details) {
        return ErrorMessage.builder()
                .message(message)
                .details(details)
                .build();
    }

    public static ErrorMessage of(String message, Integer status, String path) {
        return ErrorMessage.builder()
                .message(message)
                .status(status)
                .path(path)
                .build();
    }

    public static ErrorMessage validation(String message, List<FieldError> fieldErrors) {
        return ErrorMessage.builder()
                .message(message)
                .errorCode("VALIDATION_ERROR")
                .fieldErrors(fieldErrors)
                .build();
    }
}