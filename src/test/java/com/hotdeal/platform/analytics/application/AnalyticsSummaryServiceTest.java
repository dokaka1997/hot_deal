package com.hotdeal.platform.analytics.application;

import com.hotdeal.platform.analytics.api.dto.AnalyticsSummaryQueryRequest;
import com.hotdeal.platform.analytics.api.dto.AnalyticsSummaryResponse;
import com.hotdeal.platform.analytics.api.mapper.AnalyticsMapper;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import com.hotdeal.platform.deal.persistence.repository.DealRepository;
import com.hotdeal.platform.deal.persistence.repository.projection.CategoryDealCountProjection;
import com.hotdeal.platform.deal.persistence.repository.projection.SourceDealCountProjection;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import com.hotdeal.platform.support.fixture.DealEntityTestBuilder;
import com.hotdeal.platform.support.fixture.SourceEntityTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsSummaryServiceTest {

    @Mock
    private DealRepository dealRepository;

    private AnalyticsSummaryService analyticsSummaryService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-04-15T00:00:00Z"), ZoneOffset.UTC);
        analyticsSummaryService = new AnalyticsSummaryService(dealRepository, new AnalyticsMapper(), fixedClock);
    }

    @Test
    void getSummary_shouldReturnAggregatedMetricsForActiveDeals() {
        AnalyticsSummaryQueryRequest request = new AnalyticsSummaryQueryRequest();
        request.setActiveOnly(true);
        request.setSourceLimit(5);
        request.setCategoryLimit(5);
        request.setHottestLimit(3);

        SourceEntity source = SourceEntityTestBuilder.aSource()
                .withCode("mock_deals")
                .withName("Mock Deals")
                .build();
        DealEntity hottestDeal = DealEntityTestBuilder.aDeal()
                .withSource(source)
                .withSourceDealId("d-1")
                .withDealScore(new BigDecimal("95.00"))
                .build();

        when(dealRepository.countByStatus(DealStatus.ACTIVE)).thenReturn(12L);
        when(dealRepository.countByStatus(DealStatus.EXPIRED)).thenReturn(4L);
        when(dealRepository.averageDiscountPercent(DealStatus.ACTIVE)).thenReturn(18.456d);
        when(dealRepository.summarizeDealCountBySource(DealStatus.ACTIVE, org.springframework.data.domain.PageRequest.of(0, 5)))
                .thenReturn(List.of(new SourceCount("mock_deals", "Mock Deals", 12L)));
        when(dealRepository.summarizeDealCountByCategory(DealStatus.ACTIVE, org.springframework.data.domain.PageRequest.of(0, 5)))
                .thenReturn(List.of(new CategoryCount("electronics", 7L)));
        when(dealRepository.findHottestDeals(DealStatus.ACTIVE, org.springframework.data.domain.PageRequest.of(0, 3)))
                .thenReturn(List.of(hottestDeal));

        AnalyticsSummaryResponse response = analyticsSummaryService.getSummary(request);

        assertThat(response.activeOnly()).isTrue();
        assertThat(response.activeDealCount()).isEqualTo(12L);
        assertThat(response.expiredDealCount()).isEqualTo(4L);
        assertThat(response.averageDiscountPercent()).isEqualByComparingTo("18.46");
        assertThat(response.dealCountBySource()).hasSize(1);
        assertThat(response.dealCountBySource().getFirst().sourceCode()).isEqualTo("mock_deals");
        assertThat(response.dealCountByCategory()).hasSize(1);
        assertThat(response.dealCountByCategory().getFirst().category()).isEqualTo("electronics");
        assertThat(response.hottestDeals()).hasSize(1);
        assertThat(response.hottestDeals().getFirst().title()).isEqualTo(hottestDeal.getTitle());
        assertThat(response.generatedAt()).isEqualTo(OffsetDateTime.parse("2026-04-15T00:00:00Z"));
    }

    @Test
    void getSummary_shouldUseAllStatusesWhenActiveOnlyIsFalse() {
        AnalyticsSummaryQueryRequest request = new AnalyticsSummaryQueryRequest();
        request.setActiveOnly(false);
        request.setSourceLimit(2);
        request.setCategoryLimit(2);
        request.setHottestLimit(2);

        when(dealRepository.countByStatus(DealStatus.ACTIVE)).thenReturn(8L);
        when(dealRepository.countByStatus(DealStatus.EXPIRED)).thenReturn(5L);
        when(dealRepository.averageDiscountPercent(null)).thenReturn(11.1d);
        when(dealRepository.summarizeDealCountBySource(org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of());
        when(dealRepository.summarizeDealCountByCategory(org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of());
        when(dealRepository.findHottestDeals(org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of());

        AnalyticsSummaryResponse response = analyticsSummaryService.getSummary(request);

        assertThat(response.activeOnly()).isFalse();
        assertThat(response.averageDiscountPercent()).isEqualByComparingTo("11.10");

        ArgumentCaptor<DealStatus> statusCaptor = ArgumentCaptor.forClass(DealStatus.class);
        verify(dealRepository).averageDiscountPercent(statusCaptor.capture());
        assertThat(statusCaptor.getValue()).isNull();
    }

    private record SourceCount(String sourceCode, String sourceName, long dealCount) implements SourceDealCountProjection {
    }

    private record CategoryCount(String category, long dealCount) implements CategoryDealCountProjection {
    }
}
