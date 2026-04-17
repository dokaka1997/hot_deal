# Hot Deal Platform Backend

Backend service for collecting, normalizing, deduplicating, scoring, and exposing deal data for web/mobile clients and internal operations.

## Current Implementation Scope

Implemented through Phase 13 foundation:

- Source ingestion framework (collector registry, scheduler, retry, raw persistence)
- Normalization pipeline with mapper abstraction and validation
- Product linking + deduplication strategy
- Price history snapshots + rule-based deal scoring
- Public deal APIs (`/api/v1/deals`)
- Analytics summary APIs (`/api/v1/analytics`, `/api/v1/admin/analytics`)
- Admin monitoring APIs (`/api/v1/admin/*`)
- Alert rule CRUD + matching + delivery log abstraction
- Flyway schema migrations (`V1`, `V2`, `V3`)
- Test baseline (unit, controller slice, repository integration via Testcontainers)

## Tech Stack

- Java 21
- Spring Boot 3.5.8
- Gradle (Kotlin DSL)
- Spring Data JPA (PostgreSQL)
- Spring Data Redis
- Flyway
- Springdoc OpenAPI 3
- Spring Security (HTTP Basic for admin paths)
- Spring Boot Actuator
- Docker / docker-compose
- JUnit 5, Mockito, Testcontainers

## Architecture Style

The codebase is a modular monolith under `com.hotdeal.platform` with domain-aligned packages:

- `ingestion`: collectors, orchestration, raw data lifecycle
- `deal`: query, dedup, pricing, scoring, persistence
- `product`: canonical product persistence
- `alert`: alert rules, matcher, notifier abstraction
- `analytics`: dashboard/homepage aggregation
- `admin`: operations-focused APIs
- `common`: API envelope, pagination, exceptions, logging
- `config`: security, OpenAPI, scheduling, validation, clock

Detailed architecture documentation: [docs/architecture-overview.md](docs/architecture-overview.md)

## Prerequisites

- JDK 21 (`JAVA_HOME` must point to JDK 21)
- Docker Desktop (or Docker Engine + Compose plugin)

## Local Run

1) Start dependencies:

```bash
docker compose up -d
```

2) Run service (default profile is `local`):

```bash
./gradlew bootRun
```

Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

3) Verify runtime:

- Health: `http://localhost:8080/actuator/health`
- Readiness: `http://localhost:8080/actuator/health/readiness`
- Swagger UI: `http://localhost:8080/swagger-ui`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Configuration Profiles

- `application.yml`: shared defaults and feature flags
- `application-local.yml`: local DB/Redis, in-memory Quartz job store
- `application-test.yml`: test profile defaults
- `application-prod.yml`: production placeholders (env-driven)

Important env vars:

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`
- `APP_ADMIN_USERNAME`, `APP_ADMIN_PASSWORD`
- `APP_SERVICE_USERNAME`, `APP_SERVICE_PASSWORD`
- Scheduler toggles:
  - `INGESTION_SCHEDULER_ENABLED`
  - `NORMALIZATION_SCHEDULER_ENABLED`
  - `SCORING_SCHEDULER_ENABLED`

## Build and Test

Build:

```bash
./gradlew clean build
```

Run all tests:

```bash
./gradlew test
```

Run one test class:

```bash
./gradlew test --tests "com.hotdeal.platform.deal.persistence.repository.DealRepositoryIntegrationTest"
```

Notes:

- Repository integration tests use Testcontainers and require Docker.
- If `JAVA_HOME` is missing, Gradle commands will fail immediately.

## API Surface (Implemented)

Public:

- `GET /api/v1/deals`
- `GET /api/v1/deals/{dealId}`
- `GET /api/v1/analytics/summary`
- `POST /api/v1/alerts/rules`
- `GET /api/v1/alerts/rules`
- `POST /api/v1/alerts/rules/{alertRuleId}/disable`
- `DELETE /api/v1/alerts/rules/{alertRuleId}`

Admin (`HTTP Basic`, role `ADMIN`):

- `GET /api/v1/admin/collector-jobs`
- `GET /api/v1/admin/sources/health`
- `GET /api/v1/admin/raw-deals/failed`
- `POST /api/v1/admin/raw-deals/{rawDealId}/reprocess`
- `GET /api/v1/admin/analytics/summary`

See complete API notes: [docs/api-design.md](docs/api-design.md)

## Scheduler Jobs (Default Cron)

- Collector: `0 */5 * * * *`
- Normalization: `30 */5 * * * *`
- Scoring refresh: `15 */10 * * * *`

## Operations and Troubleshooting

- Runbook: [docs/operations-runbook.md](docs/operations-runbook.md)
- Business flows: [docs/business-flows.md](docs/business-flows.md)
- Testing details: [docs/testing-strategy.md](docs/testing-strategy.md)

## Current Limitations

- Authentication/authorization is minimal (admin HTTP Basic only; public + alert endpoints open)
- No dedicated search engine yet (query uses JPA specifications on PostgreSQL)
- Analytics uses on-demand SQL aggregation (no pre-aggregation/materialized view)
- Notification delivery is placeholder-driven (`INTERNAL_LOG`, `EMAIL` placeholder)
- No user account model yet; alerts keyed by `subscriberKey`

## Next Steps

Prioritized enhancements are documented in [docs/future-enhancements.md](docs/future-enhancements.md).
