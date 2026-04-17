package com.hotdeal.platform.ingestion.normalization.mapper;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RawDealMapperRegistry {

    private final List<RawDealMapper> rawDealMappers;

    public RawDealMapperRegistry(List<RawDealMapper> rawDealMappers) {
        this.rawDealMappers = rawDealMappers;
    }

    public RawDealMapper resolve(String sourceCode) {
        return rawDealMappers.stream()
                .filter(mapper -> mapper.supports(sourceCode))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No raw deal mapper found for source code: " + sourceCode));
    }
}
