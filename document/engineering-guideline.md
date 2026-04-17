# Backend Engineering Guideline

## Naming and Packaging
- Root package: `com.hotdeal.platform`.
- Layering per module:
  - `controller`
  - `service`
  - `repository`
  - `entity`
  - `dto`
  - `mapper`
- Keep cross-cutting code in `common`.

## API Design
- Return `ApiResponse<T>` for all controllers.
- Validate incoming requests with Jakarta Validation annotations.
- Use global exception mapping only (no controller-local exception handling).
- Keep endpoint versioning as `/api/v1/...`.

## Exception Rules
- Domain-level checked through typed runtime exceptions extending `ApiException`.
- Mandatory error code for every business exception.
- Never return stack traces in API responses.

## Logging Rules
- Use parameterized logs.
- Include correlation id (`traceId`) in every request log.
- Log business failures at `WARN`, system failures at `ERROR`.

## Transaction Rules
- Annotate write services with `@Transactional`.
- Read methods default to `@Transactional(readOnly = true)` where needed.

## Testing Rules
- Unit test service logic with Mockito.
- Repository tests use Testcontainers PostgreSQL.
- Keep fixture data explicit and deterministic.

## Configuration and Secrets
- All credentials from environment variables.
- Never commit production secrets.
- Separate profiles: `local`, `test`, `prod`.
