package com.hotdeal.platform.deal.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PublicDealSourceResponse", description = "Source information for a deal.")
public record PublicDealSourceResponse(
        @Schema(example = "mock_deals")
        String code,
        @Schema(example = "Mock Deals")
        String name,
        @Schema(example = "https://mock.example")
        String baseUrl
) {
}
