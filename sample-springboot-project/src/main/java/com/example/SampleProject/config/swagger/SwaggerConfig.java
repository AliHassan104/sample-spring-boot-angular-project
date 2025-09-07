package com.example.SampleProject.config.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${app.name:Question Bank API}")
    private String appName;

    @Value("${app.description:Question Bank Management System API}")
    private String appDescription;

    @Value("${app.version:1.0}")
    private String appVersion;

    @Value("${app.contact.name:Question Bank Team}")
    private String contactName;

    @Value("${app.contact.url:https://www.SampleProject.com}")
    private String contactUrl;

    @Value("${app.contact.email:support@questionbank.com}")
    private String contactEmail;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.SampleProject.controller"))
                .paths(PathSelectors.regex("/api.*"))
                .build()
                .apiInfo(apiInfoMetaData())
                .securityContexts(securityContexts())
                .securitySchemes(Arrays.asList(apiKey(), basicAuth()))
                .tags(
                        new Tag("Authentication", "Authentication and Authorization APIs"),
                        new Tag("Classes", "Class management APIs"),
                        new Tag("Subjects", "Subject management APIs"),
                        new Tag("Chapters", "Chapter management APIs"),
                        new Tag("Questions", "Question management APIs"),
                        new Tag("MCQ Options", "MCQ Options management APIs"),
                        new Tag("Users", "User management APIs"),
                        new Tag("Roles", "Role management APIs"),
                        new Tag("Permissions", "Permission management APIs"),
                        new Tag("Reports", "Report generation APIs")
                );
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
    }

    private BasicAuth basicAuth() {
        return new BasicAuth("basicAuth");
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }

    private List<SecurityContext> securityContexts() {
        return Arrays.asList(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/api(?!/login).*")) // Apply to all paths except /api/login
                .build());
    }

    private ApiInfo apiInfoMetaData() {
        return new ApiInfoBuilder()
                .title(appName)
                .description(appDescription)
                .contact(new Contact(contactName, contactUrl, contactEmail))
                .license("MIT License")
                .licenseUrl("https://opensource.org/licenses/MIT")
                .version(appVersion)
                .build();
    }
}