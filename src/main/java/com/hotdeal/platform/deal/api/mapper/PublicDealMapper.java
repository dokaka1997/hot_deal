package com.hotdeal.platform.deal.api.mapper;

import com.hotdeal.platform.deal.api.dto.PublicDealDetailResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealListItemResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealProductResponse;
import com.hotdeal.platform.deal.api.dto.PublicDealSourceResponse;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.product.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class PublicDealMapper {

    public PublicDealListItemResponse toListItemResponse(DealEntity deal) {
        return new PublicDealListItemResponse(
                deal.getId(),
                deal.getTitle(),
                deal.getBrand(),
                deal.getCategory(),
                deal.getImageUrl(),
                deal.getExternalUrl(),
                deal.getCurrency(),
                deal.getOriginalPrice(),
                deal.getDealPrice(),
                deal.getDiscountPercent(),
                deal.getDealScore(),
                deal.getStatus(),
                deal.getValidUntil(),
                deal.getLastSeenAt(),
                new PublicDealSourceResponse(
                        deal.getSource() == null ? null : deal.getSource().getCode(),
                        deal.getSource() == null ? null : deal.getSource().getName()
                )
        );
    }

    public PublicDealDetailResponse toDetailResponse(DealEntity deal) {
        ProductEntity product = deal.getProduct();
        PublicDealProductResponse productResponse = product == null
                ? null
                : new PublicDealProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory()
        );

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
                new PublicDealSourceResponse(
                        deal.getSource() == null ? null : deal.getSource().getCode(),
                        deal.getSource() == null ? null : deal.getSource().getName()
                ),
                productResponse
        );
    }
}
