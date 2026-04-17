CREATE INDEX IF NOT EXISTS idx_product_normalized_name_brand
    ON product (normalized_name, brand);

CREATE INDEX IF NOT EXISTS idx_deal_source_dedupe_key_last_seen
    ON deal (source_id, dedupe_key, last_seen_at DESC);
