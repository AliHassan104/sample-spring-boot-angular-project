package com.example.SampleProject.config.swagger;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ApiResponseModels {

    @ApiModel(description = "Standard API error response")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {

        @ApiModelProperty(value = "Success status", example = "false")
        private boolean success;

        @ApiModelProperty(value = "Error message", example = "Resource not found")
        private String message;

        @ApiModelProperty(value = "HTTP status code", example = "404")
        private int status;

        @ApiModelProperty(value = "Request path", example = "/api/questions/999")
        private String path;

        @ApiModelProperty(value = "Timestamp of error", example = "2025-01-20T10:30:00")
        private LocalDateTime timestamp;
    }

    @ApiModel(description = "Standard API success response")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuccessResponse<T> {

        @ApiModelProperty(value = "Success status", example = "true")
        private boolean success;

        @ApiModelProperty(value = "Success message", example = "Operation completed successfully")
        private String message;

        @ApiModelProperty(value = "Response data")
        private T data;

        @ApiModelProperty(value = "Timestamp", example = "2025-01-20T10:30:00")
        private LocalDateTime timestamp;
    }
}