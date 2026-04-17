package com.hotdeal.platform.ingestion.collector;

import java.util.List;

public interface DealCollector {

    String sourceCode();

    List<CollectedRawDeal> collect(CollectorContext context);

    default String collectorName() {
        return getClass().getSimpleName();
    }
}
