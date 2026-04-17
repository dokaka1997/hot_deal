package com.hotdeal.platform.deal.api.dto;

import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(name = "PublicDealDetailResponse", description = "Detailed response for a single deal.")
public record PublicDealDetailResponse(
        @Schema(example = "9021")
        Long id,
        String title,
        String normalizedTitle,
        String description,
        String brand,
        String category,
        String imageUrl,
        String externalUrl,
        String couponCode,
        String currency,
        BigDecimal originalPrice,
        BigDecimal dealPrice,
        BigDecimal discountPercent,
        BigDecimal dealScore,
        DealStatus status,
        OffsetDateTime validFrom,
        OffsetDateTime validUntil,
        OffsetDateTime firstSeenAt,
        OffsetDateTime lastSeenAt,
        PublicDealSourceResponse source,
        PublicDealProductResponse product
) {
}
