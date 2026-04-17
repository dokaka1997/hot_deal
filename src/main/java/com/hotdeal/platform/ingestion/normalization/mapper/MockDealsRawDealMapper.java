package com.hotdeal.platform.ingestion.normalization.mapper;

import com.hotdeal.platform.ingestion.collector.mock.MockDealsCollector;
import com.hotdeal.platform.ingestion.normalization.model.NormalizedDealRecord;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealEntity;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
@Primary
@Order(10)
public class MockDealsRawDealMapper extends GenericRawDealMapper {

    public MockDealsRawDealMapper(Clock clock) {
        super(clock);
    }

    @Override
    public boolean supports(String sourceCode) {
        return MockDealsCollector.SOURCE_CODE.equals(sourceCode);
    }

    @Override
    public NormalizedDealRecord map(SourceEntity source, RawDealEntity rawDeal) {
        // Reuse generic mapping path while keeping this mapper as explicit extension point
        // for source-specific transformations when the mock schema diverges.
        return super.map(source, rawDeal);
    }
}
