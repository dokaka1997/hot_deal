package com.hotdeal.platform.deal.persistence.repository.projection;

public interface SourceDealCountProjection {

    String getSourceCode();

    String getSourceName();

    long getDealCount();
}
