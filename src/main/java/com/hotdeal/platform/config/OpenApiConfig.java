package com.hotdeal.platform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BASIC_AUTH_SCHEME = "basicAuth";

    @Bean
    public OpenAPI hotDealOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hot Deal Platform API")
                        .version("v1")
                        .description("Backend API for promotions, deals, vouchers, and admin operations."))
                .components(new Components()
                        .addSecuritySchemes(BASIC_AUTH_SCHEME,
                                new SecurityScheme()
                                        .name(BASIC_AUTH_SCHEME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/v1/**")
                .pathsToExclude("/api/v1/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/api/v1/admin/**")
                .build();
    }
}
