# API Document (v1)

## Base URL
- Local: `http://localhost:8080`
- API prefix: `/api/v1`

## Authentication
- Public endpoints: no auth (MVP).
- Admin endpoints: HTTP Basic (`ROLE_ADMIN`).
- Correlation header supported on all endpoints: `X-Correlation-Id`.

## Standard Success Response
```json
{
  "timestamp": "2026-04-15T20:10:00Z",
  "success": true,
  "path": "/api/v1/deals",
  "traceId": "f5af00d5-1ef4-4885-b808-6372d9f32213",
  "data": {},
  "error": null
}
```

## Standard Error Response
```json
{
  "timestamp": "2026-04-15T20:10:00Z",
  "success": false,
  "path": "/api/v1/deals",
  "traceId": "f5af00d5-1ef4-4885-b808-6372d9f32213",
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Request payload is invalid.",
    "fieldErrors": [
      { "field": "keyword", "message": "must not be blank", "rejectedValue": "" }
    ]
  }
}
```

## Initial Endpoints Available
- `GET /actuator/health`
- `GET /actuator/info`
- `GET /api-docs`
- `GET /swagger-ui`

## Planned Public Endpoints
- `GET /api/v1/deals`
- `GET /api/v1/deals/{dealId}`
- `GET /api/v1/deals/search`
- `GET /api/v1/deals/{dealId}/price-history`
- `GET /api/v1/coupons`
- `GET /api/v1/analytics/summary`

## Planned Admin Endpoints
- `GET /api/v1/admin/ingestion/jobs`
- `GET /api/v1/admin/sources/health`
- `GET /api/v1/admin/raw-deals/failed`
- `POST /api/v1/admin/raw-deals/{rawDealId}/reprocess`

## Pagination Convention
- Query params:
  - `page` (0-based)
  - `size` (default 20, max 100)
  - `sort` format: `field,asc|desc`
- Response:
  - `content`
  - `page`
  - `size`
  - `totalElements`
  - `totalPages`

## Validation Convention
- Invalid payload/body: `VALIDATION_ERROR`
- Invalid query/path params: `CONSTRAINT_VIOLATION`
- Domain errors use typed codes:
  - `DEAL_NOT_FOUND`
  - `SOURCE_NOT_ACTIVE`
  - `RAW_RECORD_REPROCESS_CONFLICT`
