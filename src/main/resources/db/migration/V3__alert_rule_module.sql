CREATE TABLE alert_rule (
    id BIGSERIAL PRIMARY KEY,
    subscriber_key VARCHAR(128) NOT NULL,
    name VARCHAR(255),
    keyword VARCHAR(255),
    category VARCHAR(120),
    source_code VARCHAR(64),
    max_price NUMERIC(18, 2),
    min_discount_percent NUMERIC(5, 2),
    notification_channel VARCHAR(32) NOT NULL,
    notification_target VARCHAR(255),
    status VARCHAR(32) NOT NULL,
    last_triggered_at TIMESTAMPTZ,
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_alert_rule_status CHECK (status IN ('ACTIVE', 'DISABLED')),
    CONSTRAINT ck_alert_rule_channel CHECK (notification_channel IN ('INTERNAL_LOG', 'EMAIL')),
    CONSTRAINT ck_alert_rule_max_price_non_negative CHECK (max_price IS NULL OR max_price >= 0),
    CONSTRAINT ck_alert_rule_min_discount_percent_range CHECK (
        min_discount_percent IS NULL OR (min_discount_percent >= 0 AND min_discount_percent <= 100)
    )
);

CREATE INDEX idx_alert_rule_subscriber_status ON alert_rule (subscriber_key, status);
CREATE INDEX idx_alert_rule_source_status ON alert_rule (source_code, status);
CREATE INDEX idx_alert_rule_created_at ON alert_rule (created_at DESC);

CREATE TABLE alert_delivery_log (
    id BIGSERIAL PRIMARY KEY,
    alert_rule_id BIGINT NOT NULL,
    deal_id BIGINT NOT NULL,
    notification_channel VARCHAR(32) NOT NULL,
    delivery_status VARCHAR(32) NOT NULL,
    delivered_at TIMESTAMPTZ,
    error_message TEXT,
    payload JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_alert_delivery_log_rule FOREIGN KEY (alert_rule_id) REFERENCES alert_rule (id) ON DELETE CASCADE,
    CONSTRAINT fk_alert_delivery_log_deal FOREIGN KEY (deal_id) REFERENCES deal (id) ON DELETE CASCADE,
    CONSTRAINT ck_alert_delivery_log_channel CHECK (notification_channel IN ('INTERNAL_LOG', 'EMAIL')),
    CONSTRAINT ck_alert_delivery_log_status CHECK (delivery_status IN ('SENT', 'FAILED'))
);

CREATE INDEX idx_alert_delivery_log_rule_created ON alert_delivery_log (alert_rule_id, created_at DESC);
CREATE INDEX idx_alert_delivery_log_deal_created ON alert_delivery_log (deal_id, created_at DESC);
CREATE UNIQUE INDEX uk_alert_delivery_log_rule_deal_sent
    ON alert_delivery_log (alert_rule_id, deal_id)
    WHERE delivery_status = 'SENT';
