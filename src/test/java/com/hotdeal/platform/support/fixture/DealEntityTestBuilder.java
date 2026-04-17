package com.hotdeal.platform.support.fixture;

import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import com.hotdeal.platform.ingestion.persistence.entity.SourceEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class DealEntityTestBuilder {

    private SourceEntity source;
    private String sourceDealId = "deal-" + UUID.randomUUID();
    private String title = "Sample Deal";
    private String normalizedTitle = "sample deal";
    private String brand = "TestBrand";
    private String category = "electronics";
    private String externalUrl = "https://example.com/deal";
    private String imageUrl = "https://example.com/image.jpg";
    private String currency = "USD";
    private BigDecimal originalPrice = new BigDecimal("120.00");
    private BigDecimal dealPrice = new BigDecimal("100.00");
    private BigDecimal discountPercent = new BigDecimal("16.67");
    private BigDecimal dealScore = new BigDecimal("50.00");
    private DealStatus status = DealStatus.ACTIVE;
    private OffsetDateTime firstSeenAt = OffsetDateTime.parse("2026-01-01T00:00:00Z");
    private OffsetDateTime lastSeenAt = OffsetDateTime.parse("2026-01-02T00:00:00Z");

    private DealEntityTestBuilder() {
    }

    public static DealEntityTestBuilder aDeal() {
        return new DealEntityTestBuilder();
    }

    public DealEntityTestBuilder withSource(SourceEntity source) {
        this.source = source;
        return this;
    }

    public DealEntityTestBuilder withSourceDealId(String sourceDealId) {
        this.sourceDealId = sourceDealId;
        return this;
    }

    public DealEntityTestBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public DealEntityTestBuilder withNormalizedTitle(String normalizedTitle) {
        this.normalizedTitle = normalizedTitle;
        return this;
    }

    public DealEntityTestBuilder withBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public DealEntityTestBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public DealEntityTestBuilder withOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
        return this;
    }

    public DealEntityTestBuilder withDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
        return this;
    }

    public DealEntityTestBuilder withDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
        return this;
    }

    public DealEntityTestBuilder withDealScore(BigDecimal dealScore) {
        this.dealScore = dealScore;
        return this;
    }

    public DealEntityTestBuilder withStatus(DealStatus status) {
        this.status = status;
        return this;
    }

    public DealEntityTestBuilder withFirstSeenAt(OffsetDateTime firstSeenAt) {
        this.firstSeenAt = firstSeenAt;
        return this;
    }

    public DealEntityTestBuilder withLastSeenAt(OffsetDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
        return this;
    }

    public DealEntity build() {
        DealEntity deal = new DealEntity();
        deal.setSource(source);
        deal.setSourceDealId(sourceDealId);
        deal.setTitle(title);
        deal.setNormalizedTitle(normalizedTitle);
        deal.setBrand(brand);
        deal.setCategory(category);
        deal.setExternalUrl(externalUrl);
        deal.setImageUrl(imageUrl);
        deal.setCurrency(currency);
        deal.setOriginalPrice(originalPrice);
        deal.setDealPrice(dealPrice);
        deal.setDiscountPercent(discountPercent);
        deal.setDealScore(dealScore);
        deal.setStatus(status);
        deal.setFirstSeenAt(firstSeenAt);
        deal.setLastSeenAt(lastSeenAt);
        return deal;
    }
}
