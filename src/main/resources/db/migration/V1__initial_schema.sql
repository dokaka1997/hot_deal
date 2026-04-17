CREATE TABLE source (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    base_url VARCHAR(1024),
    schedule_cron VARCHAR(120),
    rate_limit_per_minute INTEGER,
    metadata JSONB,
    last_success_at TIMESTAMPTZ,
    last_failure_at TIMESTAMPTZ,
    last_error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_source_code UNIQUE (code),
    CONSTRAINT ck_source_type CHECK (type IN ('API', 'SCRAPER', 'FEED', 'MANUAL')),
    CONSTRAINT ck_source_status CHECK (status IN ('ACTIVE', 'PAUSED', 'DISABLED'))
);

CREATE INDEX idx_source_status ON source (status);
CREATE INDEX idx_source_type_status ON source (type, status);

CREATE TABLE collector_job_execution (
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT NOT NULL,
    job_name VARCHAR(120) NOT NULL,
    trigger_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    run_key VARCHAR(128) NOT NULL,
    started_at TIMESTAMPTZ NOT NULL,
    finished_at TIMESTAMPTZ,
    duration_ms BIGINT,
    total_fetched INTEGER NOT NULL DEFAULT 0,
    total_persisted INTEGER NOT NULL DEFAULT 0,
    total_failed INTEGER NOT NULL DEFAULT 0,
    error_message TEXT,
    details JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_collector_job_execution_source FOREIGN KEY (source_id) REFERENCES source (id),
    CONSTRAINT uk_collector_job_execution_run_key UNIQUE (run_key),
    CONSTRAINT ck_collector_job_execution_status CHECK (status IN ('RUNNING', 'SUCCESS', 'FAILED', 'PARTIAL', 'CANCELLED')),
    CONSTRAINT ck_collector_job_execution_trigger CHECK (trigger_type IN ('SCHEDULED', 'MANUAL', 'RETRY'))
);

CREATE INDEX idx_collector_job_execution_source_started ON collector_job_execution (source_id, started_at DESC);
CREATE INDEX idx_collector_job_execution_status_started ON collector_job_execution (status, started_at DESC);

CREATE TABLE raw_deal (
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT NOT NULL,
    job_execution_id BIGINT,
    source_deal_id VARCHAR(255),
    source_record_key VARCHAR(255),
    source_record_hash VARCHAR(128),
    payload JSONB NOT NULL,
    payload_version VARCHAR(32),
    status VARCHAR(32) NOT NULL,
    ingested_at TIMESTAMPTZ NOT NULL,
    normalized_at TIMESTAMPTZ,
    parse_error TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_raw_deal_source FOREIGN KEY (source_id) REFERENCES source (id),
    CONSTRAINT fk_raw_deal_job_execution FOREIGN KEY (job_execution_id) REFERENCES collector_job_execution (id) ON DELETE SET NULL,
    CONSTRAINT ck_raw_deal_status CHECK (status IN ('NEW', 'NORMALIZED', 'REJECTED', 'DUPLICATE', 'ERROR'))
);

CREATE UNIQUE INDEX uk_raw_deal_source_record_key ON raw_deal (source_id, source_record_key) WHERE source_record_key IS NOT NULL;
CREATE UNIQUE INDEX uk_raw_deal_source_record_hash ON raw_deal (source_id, source_record_hash) WHERE source_record_hash IS NOT NULL;
CREATE INDEX idx_raw_deal_source_deal_id ON raw_deal (source_id, source_deal_id);
CREATE INDEX idx_raw_deal_status_ingested ON raw_deal (status, ingested_at DESC);
CREATE INDEX idx_raw_deal_job_execution ON raw_deal (job_execution_id);

CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    canonical_sku VARCHAR(128),
    name VARCHAR(512) NOT NULL,
    normalized_name VARCHAR(512),
    brand VARCHAR(255),
    category VARCHAR(255),
    image_url VARCHAR(2048),
    fingerprint VARCHAR(128),
    attributes JSONB,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_product_canonical_sku UNIQUE (canonical_sku),
    CONSTRAINT uk_product_fingerprint UNIQUE (fingerprint)
);

CREATE INDEX idx_product_normalized_name ON product (normalized_name);
CREATE INDEX idx_product_brand ON product (brand);
CREATE INDEX idx_product_category ON product (category);

CREATE TABLE deal (
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT NOT NULL,
    product_id BIGINT,
    source_deal_id VARCHAR(255),
    fingerprint VARCHAR(128),
    dedupe_key VARCHAR(128),
    title VARCHAR(1024) NOT NULL,
    normalized_title VARCHAR(1024),
    description TEXT,
    brand VARCHAR(255),
    category VARCHAR(255),
    external_url VARCHAR(2048),
    image_url VARCHAR(2048),
    currency VARCHAR(3) NOT NULL,
    original_price NUMERIC(18, 2),
    deal_price NUMERIC(18, 2) NOT NULL,
    discount_percent NUMERIC(5, 2),
    coupon_code VARCHAR(64),
    deal_score NUMERIC(8, 2),
    status VARCHAR(32) NOT NULL,
    valid_from TIMESTAMPTZ,
    valid_until TIMESTAMPTZ,
    first_seen_at TIMESTAMPTZ NOT NULL,
    last_seen_at TIMESTAMPTZ NOT NULL,
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_deal_source FOREIGN KEY (source_id) REFERENCES source (id),
    CONSTRAINT fk_deal_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE SET NULL,
    CONSTRAINT ck_deal_status CHECK (status IN ('ACTIVE', 'EXPIRED', 'INACTIVE')),
    CONSTRAINT ck_deal_currency_len CHECK (char_length(currency) = 3),
    CONSTRAINT ck_deal_price_non_negative CHECK (deal_price >= 0),
    CONSTRAINT ck_deal_original_price_non_negative CHECK (original_price IS NULL OR original_price >= 0)
);

CREATE UNIQUE INDEX uk_deal_source_source_deal_id ON deal (source_id, source_deal_id) WHERE source_deal_id IS NOT NULL;
CREATE INDEX idx_deal_dedupe_key ON deal (dedupe_key);
CREATE INDEX idx_deal_status_valid_until ON deal (status, valid_until DESC);
CREATE INDEX idx_deal_category_status ON deal (category, status);
CREATE INDEX idx_deal_product ON deal (product_id);
CREATE INDEX idx_deal_last_seen ON deal (last_seen_at DESC);

CREATE TABLE price_history (
    id BIGSERIAL PRIMARY KEY,
    deal_id BIGINT NOT NULL,
    product_id BIGINT,
    source_id BIGINT NOT NULL,
    captured_at TIMESTAMPTZ NOT NULL,
    currency VARCHAR(3) NOT NULL,
    original_price NUMERIC(18, 2),
    deal_price NUMERIC(18, 2) NOT NULL,
    discount_percent NUMERIC(5, 2),
    availability_status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_price_history_deal FOREIGN KEY (deal_id) REFERENCES deal (id) ON DELETE CASCADE,
    CONSTRAINT fk_price_history_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE SET NULL,
    CONSTRAINT fk_price_history_source FOREIGN KEY (source_id) REFERENCES source (id),
    CONSTRAINT uk_price_history_deal_captured UNIQUE (deal_id, captured_at),
    CONSTRAINT ck_price_history_currency_len CHECK (char_length(currency) = 3),
    CONSTRAINT ck_price_history_availability_status CHECK (availability_status IN ('IN_STOCK', 'OUT_OF_STOCK', 'UNKNOWN')),
    CONSTRAINT ck_price_history_deal_price_non_negative CHECK (deal_price >= 0),
    CONSTRAINT ck_price_history_original_price_non_negative CHECK (original_price IS NULL OR original_price >= 0)
);

CREATE INDEX idx_price_history_product_captured ON price_history (product_id, captured_at DESC);
CREATE INDEX idx_price_history_source_captured ON price_history (source_id, captured_at DESC);
CREATE INDEX idx_price_history_deal_captured ON price_history (deal_id, captured_at DESC);
