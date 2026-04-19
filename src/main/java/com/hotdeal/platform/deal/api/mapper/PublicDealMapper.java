package com.hotdeal.platform.deal.api.mapper;

import com.hotdeal.platform.deal.api.dto.PublicDealDetailResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealListItemResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealPriceHistoryPointResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealPriceHistoryResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealProductResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealSourceResponse;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.PriceHistoryEntity;
import com.hotdeal.platform.product.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PublicDealMapper {

    public PublicDealListItemResponse toListItemResponse(DealEntity deal) {
        ProductEntity product = deal.getProduct();

        return new PublicDealListItemResponse(
                deal.getId(),
                deal.getTitle(),
                deal.getNormalizedTitle(),
                deal.getDescription(),
                deal.getBrand(),
                deal.getCategory(),
                deal.getImageUrl(),
                deal.getExternalUrl(),
                deal.getCouponCode(),
                deal.getCurrency(),
                deal.getOriginalPrice(),
                deal.getDealPrice(),
                deal.getDiscountPercent(),
                deal.getDealScore(),
                deal.getStatus(),
                deal.getValidFrom(),
                deal.getValidUntil(),
                deal.getFirstSeenAt(),
                deal.getLastSeenAt(),
                toSourceResponse(deal),
                toProductResponse(product)
        );
    }

    public PublicDealDetailResponse toDetailResponse(DealEntity deal) {
        ProductEntity product = deal.getProduct();

        return new PublicDealDetailResponse(
                deal.getId(),
                deal.getTitle(),
                deal.getNormalizedTitle(),
                deal.getDescription(),
                deal.getBrand(),
                deal.getCategory(),
                deal.getImageUrl(),
                deal.getExternalUrl(),
                deal.getCouponCode(),
                deal.getCurrency(),
                deal.getOriginalPrice(),
                deal.getDealPrice(),
                deal.getDiscountPercent(),
                deal.getDealScore(),
                deal.getStatus(),
                deal.getValidFrom(),
                deal.getValidUntil(),
                deal.getFirstSeenAt(),
                deal.getLastSeenAt(),
                toSourceResponse(deal),
                toProductResponse(product)
        );
    }

    public PublicDealPriceHistoryResponse toPriceHistoryResponse(Long dealId, List<PriceHistoryEntity> points) {
        return new PublicDealPriceHistoryResponse(
                dealId,
                points.stream().map(this::toPriceHistoryPointResponse).toList()
        );
    }

    private PublicDealPriceHistoryPointResponse toPriceHistoryPointResponse(PriceHistoryEntity entity) {
        return new PublicDealPriceHistoryPointResponse(
                entity.getId(),
                entity.getDeal() != null ? entity.getDeal().getId() : null,
                entity.getProduct() != null ? entity.getProduct().getId() : null,
                entity.getSource() != null ? entity.getSource().getCode() : null,
                entity.getSource() != null ? entity.getSource().getName() : null,
                entity.getCapturedAt(),
                entity.getCurrency(),
                entity.getOriginalPrice(),
                entity.getDealPrice(),
                entity.getDiscountPercent(),
                entity.getAvailabilityStatus()
        );
    }

    private PublicDealSourceResponse toSourceResponse(DealEntity deal) {
        if (deal.getSource() == null) {
            return new PublicDealSourceResponse(null, null, null);
        }
        return new PublicDealSourceResponse(
                deal.getSource().getCode(),
                deal.getSource().getName(),
                deal.getSource().getBaseUrl()
        );
    }

    private PublicDealProductResponse toProductResponse(ProductEntity product) {
        if (product == null) {
            return null;
        }
        return new PublicDealProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory()
        );
    }
}
