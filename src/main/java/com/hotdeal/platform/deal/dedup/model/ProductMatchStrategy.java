package com.hotdeal.platform.deal.dedup.model;

public enum ProductMatchStrategy {
    CANONICAL_SKU,
    FINGERPRINT,
    NORMALIZED_NAME_BRAND,
    CREATED_NEW
}
