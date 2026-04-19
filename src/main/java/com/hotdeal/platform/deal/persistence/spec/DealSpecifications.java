package com.hotdeal.platform.deal.persistence.spec;

import com.hotdeal.platform.deal.persistence.entity.DealEntity;
import com.hotdeal.platform.deal.persistence.entity.DealStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Locale;

public final class DealSpecifications {

    private DealSpecifications() {
    }

    public static Specification<DealEntity> keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        String normalized = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, builder) -> builder.or(
                builder.like(builder.lower(root.get("title")), normalized),
                builder.like(builder.lower(root.get("normalizedTitle")), normalized),
                builder.like(builder.lower(root.get("brand")), normalized),
                builder.like(builder.lower(root.get("category")), normalized)
        );
    }

    public static Specification<DealEntity> hasCategory(String category) {
        if (!StringUtils.hasText(category)) {
            return null;
        }
        String normalized = category.trim().toLowerCase(Locale.ROOT);
        return (root, query, builder) -> builder.equal(builder.lower(root.get("category")), normalized);
    }

    public static Specification<DealEntity> hasSourceCode(String sourceCode) {
        if (!StringUtils.hasText(sourceCode)) {
            return null;
        }
        String normalized = sourceCode.trim().toLowerCase(Locale.ROOT);
        return (root, query, builder) -> builder.equal(
                builder.lower(root.join("source", JoinType.INNER).get("code")),
                normalized
        );
    }

    public static Specification<DealEntity> priceGreaterThanOrEqual(BigDecimal minPrice) {
        if (minPrice == null) {
            return null;
        }
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("dealPrice"), minPrice);
    }

    public static Specification<DealEntity> priceLessThanOrEqual(BigDecimal maxPrice) {
        if (maxPrice == null) {
            return null;
        }
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("dealPrice"), maxPrice);
    }

    public static Specification<DealEntity> activeOnly(Instant now) {
        return (root, query, builder) -> builder.and(
                builder.equal(root.get("status"), DealStatus.ACTIVE),
                builder.or(
                        builder.isNull(root.get("validUntil")),
                        builder.greaterThanOrEqualTo(root.get("validUntil"), now)
                )
        );
    }

    public static Specification<DealEntity> hasCoupon(Boolean hasCoupon) {
        if (hasCoupon == null) {
            return null;
        }
        if (hasCoupon) {
            return (root, query, builder) -> builder.isNotNull(root.get("couponCode"));
        }
        return (root, query, builder) -> builder.isNull(root.get("couponCode"));
    }
}
