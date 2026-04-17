package com.hotdeal.platform.deal.dedup.model;

import com.hotdeal.platform.product.persistence.entity.ProductEntity;

public record ProductMatchResult(
        ProductEntity product,
        ProductMatchStrategy strategy,
        String traceNote
) {
}
