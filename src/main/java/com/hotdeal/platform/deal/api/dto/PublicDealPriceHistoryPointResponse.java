package com.hotdeal.platform.deal.api.dto;

import com.hotdeal.platform.deal.persistence.entity.AvailabilityStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(name = "PublicDealPriceHistoryPointResponse", description = "Single point in deal price history.")
public record PublicDealPriceHistoryPointResponse(
        @Schema(example = "12001")
        Long id,
        @Schema(example = "9021")
        Long dealId,
        @Schema(example = "501")
        Long productId,
        @Schema(example = "shopee")
        String sourceCode,
        @Schema(example = "Shopee")
        String sourceName,
        OffsetDateTime capturedAt,
        String currency,
        BigDecimal originalPrice,
        BigDecimal dealPrice,
        BigDecimal discountPercent,
        AvailabilityStatus availabilityStatus
) {
}
