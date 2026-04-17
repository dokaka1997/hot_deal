# Operations Runbook

## 1. Runtime Prerequisites

- JDK 21 (`JAVA_HOME` configured)
- PostgreSQL (schema managed by Flyway)
- Redis (readiness dependency, future cache usage)
- Docker (local/test convenience)

## 2. Local Bring-Up

Start infra:

```bash
docker compose up -d
```

Run app:

```bash
./gradlew bootRun
```

Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

## 3. Health and Diagnostics Endpoints

- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/info`
- `GET /actuator/metrics`
- `GET /actuator/prometheus`

Swagger/OpenAPI:

- `GET /swagger-ui`
- `GET /api-docs`

## 4. Key Runtime Config

### Database and Redis

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`

### Security

- `APP_ADMIN_USERNAME`, `APP_ADMIN_PASSWORD`
- `APP_SERVICE_USERNAME`, `APP_SERVICE_PASSWORD`

### Scheduler controls

- `INGESTION_SCHEDULER_ENABLED` (default `true`)
- `INGESTION_SCHEDULER_CRON` (default `0 */5 * * * *`)
- `NORMALIZATION_SCHEDULER_ENABLED` (default `true`)
- `NORMALIZATION_SCHEDULER_CRON` (default `30 */5 * * * *`)
- `SCORING_SCHEDULER_ENABLED` (default `true`)
- `SCORING_SCHEDULER_CRON` (default `15 */10 * * * *`)

## 5. Deployment Notes

Container build:

```bash
docker build -t hot-deal-platform:latest .
```

The image runs `java -jar /app/app.jar` as non-root user.

Profile selection:

- local default profile is `local`
- production deployments should pass `SPRING_PROFILES_ACTIVE=prod`

## 6. Flyway Migration Operations

Migrations live in:

- `src/main/resources/db/migration`

Startup behavior:

- Flyway runs automatically before JPA validation.

If startup fails on migration:

1. Read startup logs for failing migration version.
2. Inspect `flyway_schema_history` table.
3. Fix migration or apply corrective migration; do not edit already-applied production migration files in place.

## 7. Operational API Usage

### Ingestion health monitoring

- `GET /api/v1/admin/collector-jobs`
- `GET /api/v1/admin/sources/health`
- `GET /api/v1/admin/raw-deals/failed`

### Manual recovery

- `POST /api/v1/admin/raw-deals/{rawDealId}/reprocess`

Use this for records in `ERROR` or `REJECTED`.

### Analytics checks

- `GET /api/v1/admin/analytics/summary`

## 8. Common Incidents

### Incident A: Collector failures spike

Symptoms:

- many `collector_job_execution` rows with status `FAILED`
- source `last_error_message` populated

Actions:

1. Check source health endpoint.
2. Verify source credentials/network at source system.
3. Adjust retry settings (`INGESTION_RETRY_*`) if transient.
4. Confirm collector-specific parsing assumptions.

### Incident B: Normalization backlog grows

Symptoms:

- large count of `raw_deal.status=NEW`

Actions:

1. Verify normalization scheduler enabled.
2. Increase `NORMALIZATION_BATCH_SIZE` if DB headroom exists.
3. Inspect `REJECTED/ERROR` reasons via admin failed endpoint.
4. Reprocess corrected failed records as needed.

### Incident C: Analytics endpoint latency increases

Symptoms:

- `/api/v1/analytics/summary` slow response under load

Actions:

1. Check DB CPU and query execution plans.
2. Add temporary response caching.
3. Plan materialized/pre-aggregation rollout.

## 9. Logging and Correlation

- Every request receives/propagates `X-Correlation-Id`.
- Logs include `traceId`.
- Recommended operator workflow:
  1. capture request `traceId`
  2. filter logs by `traceId`
  3. correlate API request with scheduler/service errors

## 10. Security Operations

Current model:

- Admin APIs protected by HTTP Basic + `ADMIN` role.

Operational guidance:

- Store credentials as secrets, not plaintext env files in shared repos.
- Rotate admin credentials regularly.
- Restrict admin endpoint exposure at network ingress.

## 11. Backup and Data Retention (Current State)

- No automated backup policy in codebase yet.
- No retention cleanup job for `raw_deal`, `price_history`, `alert_delivery_log` yet.

Recommended immediate policy:

1. Daily PostgreSQL backup.
2. Point-in-time recovery enabled at infrastructure level.
3. Define retention windows before data volume growth.
