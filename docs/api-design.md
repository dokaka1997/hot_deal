# API Design

## 1. Conventions

### Base path and versioning

- API base: `/api/v1`
- Admin paths: `/api/v1/admin/**`

### Response envelope

All endpoints return `ApiResponse<T>`:

```json
{
  "timestamp": "2026-04-15T09:00:00Z",
  "success": true,
  "path": "/api/v1/deals",
  "traceId": "8ec7f8f9-96b4-4a4d-85ad-4b0f4ac8d1d3",
  "data": {},
  "error": null
}
```

Error payload uses:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Request payload is invalid.",
  "fieldErrors": [
    {
      "field": "maxPrice",
      "message": "Maximum price must be greater than or equal to 0.",
      "rejectedValue": "-10"
    }
  ]
}
```

### Pagination

Shared query model (`PageQuery`):

- `page` (default `0`, min `0`)
- `size` (default `20`, min `1`, max `100`)
- `sortBy` (endpoint-specific allowed values)
- `direction` (`ASC|DESC`, default `DESC`)

## 2. Security Model (Current)

- Public endpoints: open
- Admin endpoints: HTTP Basic, role `ADMIN`
- OpenAPI + health/info endpoints are public

Limitation:

- Alert endpoints are currently public; subscriber identity is provided by request body/query.

## 3. Endpoint Catalog

## Public Deals

### `GET /api/v1/deals`

Purpose: list/search deals for frontend.

Query params:

- `keyword`, `category`, `source`
- `minPrice`, `maxPrice`
- `activeOnly` (default `true`)
- `page`, `size`, `sortBy`, `direction`

Allowed `sortBy`: `lastSeenAt`, `dealPrice`, `discountPercent`, `dealScore`, `createdAt`

Example:

```http
GET /api/v1/deals?keyword=iphone&minPrice=300&maxPrice=1200&activeOnly=true&page=0&size=20&sortBy=dealScore&direction=DESC
```

### `GET /api/v1/deals/{dealId}`

Purpose: get full deal detail.

Validation:

- `dealId` must be positive.

## Analytics

### `GET /api/v1/analytics/summary`

Purpose: homepage/dashboard summary.

Query params:

- `activeOnly` (default `true`)
- `hottestLimit` (1..50, default `10`)
- `sourceLimit` (1..20, default `10`)
- `categoryLimit` (1..20, default `10`)

Returns:

- `activeDealCount`
- `expiredDealCount`
- `averageDiscountPercent`
- `dealCountBySource[]`
- `dealCountByCategory[]`
- `hottestDeals[]`

## Alert Rules

### `POST /api/v1/alerts/rules`

Purpose: create alert rule.

Required:

- `subscriberKey`
- at least one condition among:
  - `keyword`
  - `category`
  - `sourceCode`
  - `maxPrice`
  - `minDiscountPercent`

Optional:

- `name`
- `notificationChannel` (`INTERNAL_LOG` default, `EMAIL`)
- `notificationTarget`

### `GET /api/v1/alerts/rules`

Purpose: list rules (optionally by subscriber).

Query params:

- `subscriberKey`
- pagination params

### `POST /api/v1/alerts/rules/{alertRuleId}/disable`

Purpose: soft-disable alert rule.

### `DELETE /api/v1/alerts/rules/{alertRuleId}`

Purpose: delete alert rule and cascaded delivery logs.

## Admin Monitoring

### `GET /api/v1/admin/collector-jobs`

Purpose: ingestion execution history.

Filters:

- `sourceCode`
- `status` (`RUNNING|SUCCESS|FAILED|PARTIAL|CANCELLED`)
- pagination params

### `GET /api/v1/admin/sources/health`

Purpose: source-level operational health and 24h stats.

### `GET /api/v1/admin/raw-deals/failed`

Purpose: failed/rejected raw records.

Filters:

- `sourceCode`
- `status` (`ERROR|REJECTED`)
- pagination params

### `POST /api/v1/admin/raw-deals/{rawDealId}/reprocess`

Purpose: reset one failed raw record to `NEW` and run normalization for that record.

## Admin Analytics

### `GET /api/v1/admin/analytics/summary`

Purpose: same summary model as public analytics, scoped for internal dashboards.

## 4. Example Success Response (Analytics Summary)

```json
{
  "timestamp": "2026-04-15T09:05:00Z",
  "success": true,
  "path": "/api/v1/analytics/summary",
  "traceId": "4ca03f6a-056b-41b1-bec5-b542ecdd7e57",
  "data": {
    "generatedAt": "2026-04-15T09:05:00Z",
    "activeOnly": true,
    "activeDealCount": 1284,
    "expiredDealCount": 413,
    "averageDiscountPercent": 18.46,
    "dealCountBySource": [
      { "sourceCode": "MOCK_DEALS", "sourceName": "Mock Deals Source", "dealCount": 1284 }
    ],
    "dealCountByCategory": [
      { "category": "Electronics", "dealCount": 502 }
    ],
    "hottestDeals": [
      {
        "dealId": 101,
        "title": "Mock Headphones X1",
        "brand": "MockAudio",
        "category": "Electronics",
        "sourceCode": "MOCK_DEALS",
        "sourceName": "Mock Deals Source",
        "dealPrice": 59.99,
        "originalPrice": 89.99,
        "discountPercent": 33.34,
        "dealScore": 92.50,
        "lastSeenAt": "2026-04-15T09:00:00Z",
        "externalUrl": "https://mock.example/deals/1001",
        "imageUrl": null
      }
    ]
  },
  "error": null
}
```

## 5. Error Code Set

Used by `GlobalExceptionHandler` and `ErrorCode` enum:

- `INVALID_INPUT`
- `VALIDATION_ERROR`
- `CONSTRAINT_VIOLATION`
- `RESOURCE_NOT_FOUND`
- `BUSINESS_RULE_VIOLATION`
- `AUTHENTICATION_REQUIRED`
- `OPERATION_FORBIDDEN`
- `BAD_REQUEST`
- `INTERNAL_ERROR`

## 6. Current API Limitations

- No rate limiting yet.
- No ETag/conditional GET support.
- Search is DB-filter based (no external search backend).
- Alert APIs do not enforce user auth ownership yet.
