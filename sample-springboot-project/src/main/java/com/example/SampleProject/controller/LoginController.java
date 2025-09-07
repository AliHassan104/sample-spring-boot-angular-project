package com.example.SampleProject.controller;

import com.example.SampleProject.config.security.JwtUtil;
import com.example.SampleProject.dto.AuthenticationResponse;
import com.example.SampleProject.dto.LoginCredentials;
import com.example.SampleProject.dto.UserDto;
import com.example.SampleProject.service.UserService;
import com.example.SampleProject.service.impl.MyUserDetailServiceImplementation;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@Api(tags = "Authentication", description = "Authentication and Authorization operations")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MyUserDetailServiceImplementation myUserDetailService;
    private final UserService userService;

    public LoginController(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           MyUserDetailServiceImplementation myUserDetailService,
                           UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.myUserDetailService = myUserDetailService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @ApiOperation(
            value = "Authenticate user",
            notes = "Authenticate user with username/email and password to get JWT token",
            response = AuthenticationResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully authenticated", response = AuthenticationResponse.class),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 401, message = "Authentication failed - incorrect credentials"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<?> createAuthenticationToken(
            @ApiParam(value = "Login credentials", required = true)
            @Valid @RequestBody LoginCredentials loginCredentials) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginCredentials.getName(),
                            loginCredentials.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Incorrect Username or Password! ", e);
        }

        UserDetails userDetails = myUserDetailService.loadUserByUsername(loginCredentials.getName());
        String jwtToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
    }

    @PostMapping("/signup")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(
            value = "Register new user",
            notes = "Register a new user (Admin only)",
            response = String.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User registered successfully"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 403, message = "Access denied - Admin role required"),
            @ApiResponse(code = 409, message = "User already exists"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<?> signup(
            @ApiParam(value = "User registration data", required = true)
            @Valid @RequestBody UserDto userdto) {
        userService.registerUser(userdto);
        return ResponseEntity.ok("User registered successfully.");
    }
}