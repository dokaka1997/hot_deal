# Hot Deal Platform Overview

## Purpose
Hot Deal Platform aggregates deals, promotions, vouchers, and flash sales from multiple sources into a normalized domain model that can be consumed by web/mobile clients and internal operations.

## Core Business Outcomes
- Aggregate deals from fragmented retail sources.
- Normalize and deduplicate multi-source records.
- Track price history and deal lifecycle.
- Expose public APIs for listing/search/detail.
- Expose admin APIs for ingestion monitoring and reprocessing.

## Current Architecture (MVP Foundation)
- Architecture style: modular monolith.
- Runtime stack: Java 21, Spring Boot, PostgreSQL, Redis, Quartz.
- Modules:
  - `source`: source collectors + scheduling entry points
  - `raw`: raw payload persistence and ingestion metadata
  - `deal`: normalized deal model and query APIs
  - `product`: canonical product catalog
  - `price`: price history timeline
  - `alert`: alert rule and match processing
  - `admin`: operational APIs
  - `common`: cross-cutting response/error/logging utilities

## Non-Functional Baseline
- Idempotent ingestion design with source-key/hash strategy.
- Standard API response/error contract.
- Request correlation via `X-Correlation-Id` and `traceId`.
- API documentation via OpenAPI.
- Health and metrics via Actuator.

## Planned Delivery Sequence
1. Foundation (response, exception, validation, security, config).
2. Database schema + JPA mapping.
3. Collector framework + raw pipeline.
4. Normalization + dedupe + price history.
5. Public APIs + admin APIs.
6. Alert module + analytics + hardening.
