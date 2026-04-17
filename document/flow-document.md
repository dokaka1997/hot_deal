# Business and Processing Flows

## 1) Source Collection Flow
Trigger: Quartz job schedule.

```mermaid
sequenceDiagram
  participant Q as Quartz
  participant C as Collector
  participant R as Raw Service
  participant DB as PostgreSQL

  Q->>C: trigger collect(source)
  C->>R: persist raw payload batch
  R->>DB: insert raw_deal records
  R-->>Q: job stats (success/failure)
```

## 2) Normalization Flow (Planned)
Trigger: new raw records or reprocess request.

```mermaid
sequenceDiagram
  participant N as Normalizer
  participant DB as PostgreSQL
  N->>DB: read raw_deal
  N->>N: map + validate canonical fields
  N->>DB: upsert deal
```

## 3) Deduplication Flow (Planned)
Trigger: after normalization.

```mermaid
flowchart LR
  A[Normalized Deal] --> B[Generate Dedup Keys]
  B --> C[Find candidate deals/products]
  C --> D{Match score >= threshold?}
  D -->|Yes| E[Merge/Link existing]
  D -->|No| F[Create new canonical deal/product]
```

## 4) Price History Update Flow (Planned)
Trigger: new normalized deal snapshot.

```mermaid
sequenceDiagram
  participant P as Price Service
  participant DB as PostgreSQL
  P->>DB: read latest price snapshot
  P->>DB: insert new price_history row if changed
```

## 5) Alert Match Flow (Planned)
Trigger: deal created/updated.

```mermaid
flowchart LR
  A[Deal update] --> B[Load active alert rules]
  B --> C[Evaluate thresholds/keywords/categories]
  C --> D[Create alert_delivery_log]
  D --> E[Dispatch notifier adapter]
```
