package com.hotdeal.platform.deal.api.dto;

import com.hotdeal.platform.common.pagination.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PublicDealPriceHistoryRequest", description = "Pagination and sorting options for deal price history.")
public class PublicDealPriceHistoryRequest extends PageQuery {

    public PublicDealPriceHistoryRequest() {
        setSortBy("capturedAt");
        setSize(100);
    }
}
