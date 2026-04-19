package com.hotdeal.platform.deal.persistence.repository;

import com.hotdeal.platform.config.JpaAuditingConfig;
import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import com.hotdeal.platform.deal.persistence.repository.projection.CategoryDealCountProjection;
import com.hotdeal.platform.deal.persistence.repository.projection.SourceDealCountProjection;
import com.hotdeal.platform.deal.persistence.spec.DealSpecifications;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;
import com.hotdeal.platform.ingestion.persistence.repository.SourceRepository;
import com.hotdeal.platform.support.PostgresContainerSupport;
import com.hotdeal.platform.support.fixture.DealEntityTestBuilder;
import com.hotdeal.platform.support.fixture.SourceEntityTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class DealRepositoryIntegrationTest extends PostgresContainerSupport {

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Test
    void analyticsQueries_shouldReturnExpectedAggregationsAndRanking() {
        SourceEntity sourceA = sourceRepository.save(SourceEntityTestBuilder.aSource()
                .withCode("source_a")
                .withName("Source A")
                .build());
        SourceEntity sourceB = sourceRepository.save(SourceEntityTestBuilder.aSource()
                .withCode("source_b")
                .withName("Source B")
                .build());

        dealRepository.saveAll(List.of(
                DealEntityTestBuilder.aDeal()
                        .withSource(sourceA)
                        .withSourceDealId("a-1")
                        .withCategory("electronics")
                        .withDiscountPercent(new BigDecimal("20.00"))
                        .withDealScore(new BigDecimal("80.00"))
                        .withStatus(DealStatus.ACTIVE)
                        .build(),
                DealEntityTestBuilder.aDeal()
                        .withSource(sourceA)
                        .withSourceDealId("a-2")
                        .withCategory("electronics")
                        .withDiscountPercent(new BigDecimal("30.00"))
                        .withDealScore(new BigDecimal("95.00"))
                        .withStatus(DealStatus.ACTIVE)
                        .build(),
                DealEntityTestBuilder.aDeal()
                        .withSource(sourceB)
                        .withSourceDealId("b-1")
                        .withCategory("home")
                        .withDiscountPercent(new BigDecimal("10.00"))
                        .withDealScore(new BigDecimal("70.00"))
                        .withStatus(DealStatus.ACTIVE)
                        .build(),
                DealEntityTestBuilder.aDeal()
                        .withSource(sourceB)
                        .withSourceDealId("b-2")
                        .withCategory("home")
                        .withDiscountPercent(new BigDecimal("50.00"))
                        .withDealScore(new BigDecimal("99.00"))
                        .withStatus(DealStatus.EXPIRED)
                        .build()
        ));

        long activeCount = dealRepository.countByStatus(DealStatus.ACTIVE);
        long expiredCount = dealRepository.countByStatus(DealStatus.EXPIRED);
        assertThat(activeCount).isEqualTo(3L);
        assertThat(expiredCount).isEqualTo(1L);

        List<SourceDealCountProjection> countBySource = dealRepository.summarizeDealCountBySource(
                DealStatus.ACTIVE,
                PageRequest.of(0, 10)
        );
        assertThat(countBySource)
                .extracting(SourceDealCountProjection::getSourceCode, SourceDealCountProjection::getDealCount)
                .containsExactly(
                        tuple("source_a", 2L),
                        tuple("source_b", 1L)
                );

        List<CategoryDealCountProjection> countByCategory = dealRepository.summarizeDealCountByCategory(
                DealStatus.ACTIVE,
                PageRequest.of(0, 10)
        );
        assertThat(countByCategory)
                .extracting(CategoryDealCountProjection::getCategory, CategoryDealCountProjection::getDealCount)
                .containsExactly(
                        tuple("electronics", 2L),
                        tuple("home", 1L)
                );

        Double averageDiscount = dealRepository.averageDiscountPercent(DealStatus.ACTIVE);
        assertThat(averageDiscount).isNotNull();
        assertThat(averageDiscount).isCloseTo(20.0, org.assertj.core.data.Offset.offset(0.001));

        List<DealEntity> hottestDeals = dealRepository.findHottestDeals(DealStatus.ACTIVE, PageRequest.of(0, 2));
        assertThat(hottestDeals)
                .extracting(DealEntity::getSourceDealId)
                .containsExactly("a-2", "a-1");
    }

    @Test
    void findAll_shouldSupportHasCouponSpecification() {
        SourceEntity source = sourceRepository.save(SourceEntityTestBuilder.aSource()
                .withCode("coupon_source")
                .withName("Coupon Source")
                .build());

        DealEntity withCoupon = DealEntityTestBuilder.aDeal()
                .withSource(source)
                .withSourceDealId("coupon-1")
                .withStatus(DealStatus.ACTIVE)
                .build();
        withCoupon.setCouponCode("SAVE30");

        DealEntity withoutCoupon = DealEntityTestBuilder.aDeal()
                .withSource(source)
                .withSourceDealId("coupon-2")
                .withStatus(DealStatus.ACTIVE)
                .build();
        withoutCoupon.setCouponCode(null);

        dealRepository.saveAll(List.of(withCoupon, withoutCoupon));

        var couponOnlyPage = dealRepository.findAll(
                DealSpecifications.hasCoupon(true),
                PageRequest.of(0, 10)
        );
        var nonCouponPage = dealRepository.findAll(
                DealSpecifications.hasCoupon(false),
                PageRequest.of(0, 10)
        );

        assertThat(couponOnlyPage.getContent())
                .extracting(DealEntity::getSourceDealId)
                .containsExactly("coupon-1");

        assertThat(nonCouponPage.getContent())
                .extracting(DealEntity::getSourceDealId)
                .contains("coupon-2")
                .doesNotContain("coupon-1");
    }
}
