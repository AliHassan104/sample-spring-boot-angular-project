package com.example.SampleProject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "User data transfer object")
public class UserDto {

    @ApiModelProperty(value = "User ID", example = "1")
    private Long id;

    @ApiModelProperty(
            value = "Username or email",
            example = "john.doe@example.com",
            required = true
    )
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String name;

    @ApiModelProperty(
            value = "User password",
            example = "securePassword123",
            required = true
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @ApiModelProperty(
            value = "User roles",
            example = "[{\"id\": 1, \"name\": \"ROLE_USER\"}]"
    )
    @Builder.Default
    private Set<RoleDto> roles = new HashSet<>();

    // Optional: Add email field if username and email are separate
    @ApiModelProperty(
            value = "User email address",
            example = "john.doe@example.com"
    )
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @ApiModelProperty(
            value = "User's full name",
            example = "John Doe"
    )
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @ApiModelProperty(
            value = "User active status",
            example = "true"
    )
    @Builder.Default
    private Boolean isActive = true;
}