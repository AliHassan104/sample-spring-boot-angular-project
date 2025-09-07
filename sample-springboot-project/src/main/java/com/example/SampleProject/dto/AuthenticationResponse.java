package com.example.SampleProject.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(description = "Authentication response containing JWT token")
public class AuthenticationResponse {

    @ApiModelProperty(
            value = "JWT access token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            required = true
    )
    private final String jwt;

    @ApiModelProperty(
            value = "Token type",
            example = "Bearer",
            required = true
    )
    private final String tokenType = "Bearer";

    @ApiModelProperty(
            value = "Token expiration time in milliseconds",
            example = "86400000",
            required = true
    )
    private final long expiresIn = 86400000; // 24 hours

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }
}