package com.hotdeal.platform.deal.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PublicDealProductResponse", description = "Canonical product information linked to a deal.")
public record PublicDealProductResponse(
        @Schema(example = "123")
        Long id,
        @Schema(example = "Apple iPhone 15 128GB")
        String name,
        @Schema(example = "APPLE")
        String brand,
        @Schema(example = "electronics")
        String category
) {
}
