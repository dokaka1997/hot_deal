package com.hotdeal.platform.deal.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "PublicDealPriceHistoryResponse", description = "Price history response for one deal.")
public record PublicDealPriceHistoryResponse(
        @Schema(example = "9021")
        Long dealId,
        List<PublicDealPriceHistoryPointResponse> points
) {
}
