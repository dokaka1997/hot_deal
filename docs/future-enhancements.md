# Future Enhancements

This roadmap is aligned with current code and production gaps.

## 1. Near-Term (Next 1-2 Iterations)

### 1.1 Security hardening

- Replace in-memory users with external identity (OIDC/JWT).
- Add ownership checks for alert rules (currently keyed by `subscriberKey` only).
- Add API rate limiting for public endpoints.

### 1.2 Analytics performance baseline

- Add Redis caching for `/api/v1/analytics/summary` and hot deal list endpoints.
- Add query metrics and latency dashboards for aggregation endpoints.

### 1.3 Data retention jobs

- Introduce scheduled cleanup/archival for:
  - old `raw_deal`
  - old `price_history`
  - old `alert_delivery_log`

### 1.4 Test coverage expansion

- Integration tests for normalization and dedup flows.
- Scheduler behavior tests.
- Security access tests for admin endpoints.

## 2. Mid-Term (Scale and Product Depth)

### 2.1 Search and retrieval

- Introduce dedicated search index (OpenSearch/Elasticsearch).
- Add relevance ranking, typo tolerance, and facet performance for high-cardinality filters.

### 2.2 Analytics architecture

- Add materialized views or pre-aggregation tables for dashboard metrics.
- Introduce refresh jobs or event-driven metric pipelines.

### 2.3 Notification channels

- Implement real notifier adapters:
  - transactional email provider
  - push/webhook channels
- Add delivery retry and dead-letter strategy.

### 2.4 Collector ecosystem

- Add connectors for real retail APIs/sitemaps/feeds.
- Add per-source throttling, circuit-breaker, and source-level quotas.

## 3. Longer-Term (Platform Evolution)

### 3.1 Personalization and accounts

- Add user account domain, subscriptions, preferences, and watchlists.
- Add personalized ranking and recommendation engine.

### 3.2 Architecture decomposition

- Split ingestion pipeline from API read path when throughput requires:
  - ingestion/normalization worker service
  - query API service
  - notification worker

### 3.3 Event-driven backbone

- Publish lifecycle events (`RawDealIngested`, `DealNormalized`, `DealScored`, `AlertMatched`) to a message broker.
- Decouple analytics and notifications from synchronous transaction boundaries.

## 4. Technical Debt and Known Gaps

Current intentional shortcuts:

- Quartz configured but scheduled jobs currently use `@Scheduled`.
- Redis provisioned but not yet used for app-level caching.
- No coupon/voucher standalone model (`coupon_code` is embedded in `deal`).
- Category taxonomy is string-based (no managed taxonomy table).

## 5. Suggested Prioritization Criteria

Use this order when selecting next work:

1. Reliability impact (data correctness, ingestion failure recovery)
2. Security risk reduction
3. Latency and cost reduction on high-traffic endpoints
4. Product value unlock (user-facing alert/search improvements)
