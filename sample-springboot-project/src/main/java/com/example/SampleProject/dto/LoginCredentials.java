package com.example.SampleProject.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
@Builder
@ApiModel(description = "Login credentials for user authentication")
public class LoginCredentials {

    @ApiModelProperty(
            value = "Username or email address",
            example = "admin@questionbank.com",
            required = true
    )
    @NotBlank(message = "Username or email is required")
    private String name;

    @ApiModelProperty(
            value = "User password",
            example = "securePassword123",
            required = true
    )
    @NotBlank(message = "Password is required")
    private String password;
}