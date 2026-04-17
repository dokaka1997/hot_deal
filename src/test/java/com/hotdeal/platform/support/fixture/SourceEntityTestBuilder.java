package com.hotdeal.platform.support.fixture;

import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import com.hotdeal.platform.ingestion.persistence.entity.SourceStatus;
import com.hotdeal.platform.ingestion.persistence.entity.SourceType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class SourceEntityTestBuilder {

    private String code = "source-" + UUID.randomUUID();
    private String name = "Test Source";
    private SourceType type = SourceType.API;
    private SourceStatus status = SourceStatus.ACTIVE;
    private String baseUrl = "https://example.com";
    private Map<String, Object> metadata = new LinkedHashMap<>();

    private SourceEntityTestBuilder() {
    }

    public static SourceEntityTestBuilder aSource() {
        return new SourceEntityTestBuilder();
    }

    public SourceEntityTestBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public SourceEntityTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SourceEntityTestBuilder withType(SourceType type) {
        this.type = type;
        return this;
    }

    public SourceEntityTestBuilder withStatus(SourceStatus status) {
        this.status = status;
        return this;
    }

    public SourceEntityTestBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public SourceEntityTestBuilder withMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public SourceEntity build() {
        SourceEntity source = new SourceEntity();
        source.setCode(code);
        source.setName(name);
        source.setType(type);
        source.setStatus(status);
        source.setBaseUrl(baseUrl);
        source.setMetadata(metadata);
        return source;
    }
}
