package com.hotdeal.platform.ingestion.normalization.mapper;

import com.hotdeal.platform.ingestion.normalization.model.NormalizedDealRecord;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealEntity;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;

public interface RawDealMapper {

    boolean supports(String sourceCode);

    NormalizedDealRecord map(SourceEntity source, RawDealEntity rawDeal);
}
