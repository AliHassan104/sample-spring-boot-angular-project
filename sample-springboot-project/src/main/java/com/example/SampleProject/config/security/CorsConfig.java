package com.example.SampleProject.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://127.0.0.1:4200}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String[] allowedMethods;

    @Value("${app.cors.max-age:3600}")
    private Long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // SECURE: Only allow specific origins (not "*")
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));

        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));

        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Content-Type",
                "Accept",
                "Authorization",
                "X-Requested-With",
                "Cache-Control",
                "X-CSRF-TOKEN"
        ));

        // Expose headers that frontend can read
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Cache preflight requests for better performance
        configuration.setMaxAge(maxAge);

        // Apply to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}