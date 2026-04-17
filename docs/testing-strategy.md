# Testing Strategy

## 1. Goals

- Validate business-critical behavior over broad shallow coverage.
- Keep tests readable, deterministic, and easy to extend.
- Separate unit concerns from database-backed behavior.

## 2. Current Test Layers

### Unit tests

Focus:

- service logic and rule evaluation
- deterministic behavior with mocked dependencies

Implemented:

- `analytics/application/AnalyticsSummaryServiceTest`
- `alert/application/AlertRuleServiceTest`
- `alert/matching/AlertRuleMatcherTest`
- `deal/api/dto/PublicDealSearchRequestValidationTest`

### Controller slice tests

Focus:

- request validation
- response envelope behavior
- error handling contract

Implemented:

- `deal/api/PublicDealControllerTest` (`@WebMvcTest`)

### Repository integration tests

Focus:

- SQL/JPA mapping correctness
- aggregation query behavior against real PostgreSQL

Implemented:

- `deal/persistence/repository/DealRepositoryIntegrationTest` (`@DataJpaTest` + Testcontainers)

## 3. Test Infrastructure

### Testcontainers

- Base class: `support/PostgresContainerSupport`
- Uses `postgres:16-alpine`
- Injects datasource properties via `@DynamicPropertySource`

### Fixtures

Reusable test data builders:

- `support/fixture/SourceEntityTestBuilder`
- `support/fixture/DealEntityTestBuilder`

These reduce setup duplication and keep test intent clear.

## 4. Execution

Run all tests:

```bash
./gradlew test
```

Run a single class:

```bash
./gradlew test --tests "com.hotdeal.platform.deal.api.PublicDealControllerTest"
```

Prerequisites:

- JDK 21 configured (`JAVA_HOME`)
- Docker running for Testcontainers-backed integration tests

## 5. Quality Gates (Recommended)

For CI pipeline:

1. `./gradlew clean test`
2. Fail build on any test failure
3. Publish test reports (`build/reports/tests/test/index.html`)
4. Add static analysis stage (SpotBugs/Checkstyle or similar) as next step

## 6. Current Gaps

Not yet covered:

- Normalization service end-to-end tests with malformed payload variants
- Dedup/product matching edge cases under concurrent inserts
- Price history/scoring scheduler behavior over large pages
- Admin and alert controller slice tests
- Security boundary tests (`/api/v1/admin/**` role enforcement)

## 7. Expansion Plan

Near-term additions:

1. Add integration tests for `DealNormalizationService` with seeded raw payloads.
2. Add unit tests for `PriceHistoryService` and `DealScoringService` formulas.
3. Add MVC tests for admin and alert APIs.
4. Introduce contract tests for API response schema stability.
