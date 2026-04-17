package com.hotdeal.platform.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        Credentials admin,
        Credentials service,
        Cors cors
) {
    private static final List<String> DEFAULT_ALLOWED_ORIGIN_PATTERNS = List.of(
            "http://localhost:*",
            "http://127.0.0.1:*"
    );
    private static final List<String> DEFAULT_ALLOWED_METHODS = List.of(
            "GET",
            "POST",
            "PUT",
            "PATCH",
            "DELETE",
            "OPTIONS"
    );
    private static final List<String> DEFAULT_ALLOWED_HEADERS = List.of("*");
    private static final List<String> DEFAULT_EXPOSED_HEADERS = List.of("X-Correlation-Id");
    private static final boolean DEFAULT_ALLOW_CREDENTIALS = true;
    private static final long DEFAULT_MAX_AGE_SECONDS = 3600L;

    public SecurityProperties {
        cors = cors == null ? new Cors(null, null, null, null, null, null) : cors;
    }

    public record Credentials(
            String username,
            String password
    ) {
    }

    public record Cors(
            List<String> allowedOriginPatterns,
            List<String> allowedMethods,
            List<String> allowedHeaders,
            List<String> exposedHeaders,
            Boolean allowCredentials,
            Long maxAgeSeconds
    ) {
        public Cors {
            allowedOriginPatterns = defaultIfEmpty(allowedOriginPatterns, DEFAULT_ALLOWED_ORIGIN_PATTERNS);
            allowedMethods = defaultIfEmpty(allowedMethods, DEFAULT_ALLOWED_METHODS);
            allowedHeaders = defaultIfEmpty(allowedHeaders, DEFAULT_ALLOWED_HEADERS);
            exposedHeaders = defaultIfEmpty(exposedHeaders, DEFAULT_EXPOSED_HEADERS);
            allowCredentials = allowCredentials == null ? DEFAULT_ALLOW_CREDENTIALS : allowCredentials;
            maxAgeSeconds = maxAgeSeconds == null ? DEFAULT_MAX_AGE_SECONDS : maxAgeSeconds;
        }
    }

    private static List<String> defaultIfEmpty(List<String> candidate, List<String> fallback) {
        return candidate == null || candidate.isEmpty() ? fallback : List.copyOf(candidate);
    }
}
