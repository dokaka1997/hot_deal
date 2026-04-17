package com.hotdeal.platform.analytics.api.mapper;

import com.hotdeal.platform.analytics.api.dto.AnalyticsDealCountByCategoryResponse;
import com.hotdeal.platform.analytics.api.dto.AnalyticsDealCountBySourceResponse;
import com.hotdeal.platform.analytics.api.dto.AnalyticsHottestDealResponse;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.repository.projection.CategoryDealCountProjection;
import com.hotdeal.platform.deal.persistence.repository.projection.SourceDealCountProjection;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsMapper {

    public AnalyticsDealCountBySourceResponse toSourceCountResponse(SourceDealCountProjection projection) {
        return new AnalyticsDealCountBySourceResponse(
                projection.getSourceCode(),
                projection.getSourceName(),
                projection.getDealCount()
        );
    }

    public AnalyticsDealCountByCategoryResponse toCategoryCountResponse(CategoryDealCountProjection projection) {
        return new AnalyticsDealCountByCategoryResponse(
                projection.getCategory(),
                projection.getDealCount()
        );
    }

    public AnalyticsHottestDealResponse toHottestDealResponse(DealEntity deal) {
        return new AnalyticsHottestDealResponse(
                deal.getId(),
                deal.getTitle(),
                deal.getBrand(),
                deal.getCategory(),
                deal.getSource() == null ? null : deal.getSource().getCode(),
                deal.getSource() == null ? null : deal.getSource().getName(),
                deal.getDealPrice(),
                deal.getOriginalPrice(),
                deal.getDiscountPercent(),
                deal.getDealScore(),
                deal.getLastSeenAt(),
                deal.getExternalUrl(),
                deal.getImageUrl()
        );
    }
}
