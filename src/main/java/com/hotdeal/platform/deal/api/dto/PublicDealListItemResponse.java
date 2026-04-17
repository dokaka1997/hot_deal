package com.hotdeal.platform.deal.api.dto;

import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(name = "PublicDealListItemResponse", description = "List item response for public deal search.")
public record PublicDealListItemResponse(
        @Schema(example = "9021")
        Long id,
        @Schema(example = "Apple iPhone 15 128GB - 20% Off")
        String title,
        String brand,
        String category,
        String imageUrl,
        String externalUrl,
        String currency,
        BigDecimal originalPrice,
        BigDecimal dealPrice,
        BigDecimal discountPercent,
        BigDecimal dealScore,
        DealStatus status,
        OffsetDateTime validUntil,
        OffsetDateTime lastSeenAt,
        PublicDealSourceResponse source
) {
}
