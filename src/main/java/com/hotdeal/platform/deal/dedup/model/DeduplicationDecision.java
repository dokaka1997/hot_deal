package com.hotdeal.platform.deal.dedup.model;

import com.hotdeal.platform.product.persistence.entity.ProductEntity;

import java.util.Map;

public record DeduplicationDecision(
        String dedupeKey,
        ProductEntity product,
        Map<String, Object> decisionTrace
) {
}
