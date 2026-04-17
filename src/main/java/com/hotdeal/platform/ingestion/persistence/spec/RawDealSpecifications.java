package com.hotdeal.platform.ingestion.persistence.spec;

import com.hotdeal.platform.ingestion.persistence.entity.RawDealEntity;
import com.hotdeal.platform.ingestion.persistence.entity.RawDealStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Locale;

public final class RawDealSpecifications {

    private RawDealSpecifications() {
    }

    public static Specification<RawDealEntity> hasStatuses(Collection<RawDealStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return (root, query, builder) -> root.get("status").in(statuses);
    }

    public static Specification<RawDealEntity> hasSourceCode(String sourceCode) {
        if (!StringUtils.hasText(sourceCode)) {
            return null;
        }
        String normalized = sourceCode.trim().toLowerCase(Locale.ROOT);
        return (root, query, builder) -> builder.equal(
                builder.lower(root.join("source", JoinType.INNER).get("code")),
                normalized
        );
    }
}
