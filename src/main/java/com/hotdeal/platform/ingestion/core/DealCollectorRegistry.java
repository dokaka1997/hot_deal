package com.hotdeal.platform.ingestion.core;

import com.hotdeal.platform.ingestion.collector.DealCollector;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DealCollectorRegistry {

    private final Map<String, DealCollector> collectorsBySourceCode;

    public DealCollectorRegistry(List<DealCollector> collectors) {
        this.collectorsBySourceCode = collectors.stream()
                .collect(Collectors.toUnmodifiableMap(
                        DealCollector::sourceCode,
                        Function.identity(),
                        (first, second) -> {
                            throw new IllegalStateException("Duplicate collector registered for source code: " + first.sourceCode());
                        }
                ));
    }

    public Optional<DealCollector> resolve(String sourceCode) {
        return Optional.ofNullable(collectorsBySourceCode.get(sourceCode));
    }

    public Set<String> supportedSourceCodes() {
        return collectorsBySourceCode.keySet();
    }
}
