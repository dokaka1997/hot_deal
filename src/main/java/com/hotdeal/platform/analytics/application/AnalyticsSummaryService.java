package com.hotdeal.platform.analytics.application;

import com.hotdeal.platform.analytics.api.dto.AnalyticsDealCountByCategoryResponse;
import com.hotdeal.platform.analytics.api.dto.AnalyticsDealCountBySourceResponse;
import com.hotdeal.platform.analytics.api.dto.AnalyticsHottestDealResponse;
import com.hotdeal.platform.analytics.api.dto.AnalyticsSummaryQueryRequest;
import com.hotdeal.platform.analytics.api.dto.AnalyticsSummaryResponse;
import com.hotdeal.platform.analytics.api.mapper.AnalyticsMapper;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import com.hotdeal.platform.deal.persistence.repository.DealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class AnalyticsSummaryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsSummaryService.class);

    private final DealRepository dealRepository;
    private final AnalyticsMapper analyticsMapper;
    private final Clock clock;

    public AnalyticsSummaryService(DealRepository dealRepository,
                                   AnalyticsMapper analyticsMapper,
                                   Clock clock) {
        this.dealRepository = dealRepository;
        this.analyticsMapper = analyticsMapper;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse getSummary(AnalyticsSummaryQueryRequest request) {
        DealStatus scopedStatus = request.isActiveOnly() ? DealStatus.ACTIVE : null;

        long activeDealCount = dealRepository.countByStatus(DealStatus.ACTIVE);
        long expiredDealCount = dealRepository.countByStatus(DealStatus.EXPIRED);

        BigDecimal averageDiscountPercent = toScaledDecimal(dealRepository.averageDiscountPercent(scopedStatus));

        List<AnalyticsDealCountBySourceResponse> dealCountBySource = dealRepository
                .summarizeDealCountBySource(scopedStatus, PageRequest.of(0, request.getSourceLimit()))
                .stream()
                .map(analyticsMapper::toSourceCountResponse)
                .toList();

        List<AnalyticsDealCountByCategoryResponse> dealCountByCategory = dealRepository
                .summarizeDealCountByCategory(scopedStatus, PageRequest.of(0, request.getCategoryLimit()))
                .stream()
                .map(analyticsMapper::toCategoryCountResponse)
                .toList();

        List<AnalyticsHottestDealResponse> hottestDeals = dealRepository
                .findHottestDeals(scopedStatus, PageRequest.of(0, request.getHottestLimit()))
                .stream()
                .map(analyticsMapper::toHottestDealResponse)
                .toList();

        LOGGER.info("analytics summary generated activeOnly={} activeDealCount={} expiredDealCount={} sourceBuckets={} categoryBuckets={} hottestCount={}",
                request.isActiveOnly(),
                activeDealCount,
                expiredDealCount,
                dealCountBySource.size(),
                dealCountByCategory.size(),
                hottestDeals.size());

        return new AnalyticsSummaryResponse(
                now(),
                request.isActiveOnly(),
                activeDealCount,
                expiredDealCount,
                averageDiscountPercent,
                dealCountBySource,
                dealCountByCategory,
                hottestDeals
        );
    }

    private BigDecimal toScaledDecimal(Double value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private OffsetDateTime now() {
        return OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }
}
