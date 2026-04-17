# FE-BE Integration Guideline

## Contract Principles
- FE must parse the top-level envelope: `success`, `data`, `error`, `traceId`.
- FE should surface `error.message` to user and log `traceId` for support.
- All pagination controls map to backend query params (`page`, `size`, `sort`).

## HTTP Headers
- Send `Accept: application/json`.
- Send `X-Correlation-Id` for user-session tracing (optional, recommended).
- For admin routes, send HTTP Basic credentials.

## Filtering and Sorting Convention
- Filter params are additive and optional.
- Sort format: `sort=field,asc` or `sort=field,desc`.
- FE should preserve sort/filter state in URL query for shareable views.

## Error Handling on FE
- `400`: validation; highlight invalid input.
- `401/403`: auth/role issue; redirect to admin login or forbidden page.
- `404`: missing resource; show not-found state.
- `500`: show fallback state with retry action and keep `traceId`.

## Date/Time and Price
- Backend uses ISO-8601 UTC timestamps.
- FE must localize date/time by user locale/timezone.
- Prices are decimal values; FE controls display currency symbol/format.

## Caching Strategy (MVP)
- Public listing endpoints can be cached by FE for short TTL.
- Admin pages should prefer fresh data.

## Versioning and Backward Compatibility
- FE integrates against `/api/v1`.
- Breaking changes require new API version path (`/api/v2`).
