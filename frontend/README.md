# Hot Deal Frontend

Phase 1 foundation for the Hot Deal web client using:

- Next.js (App Router)
- TypeScript
- Tailwind CSS
- ESLint

## 1) Prerequisites

- Node.js 20+ (recommended)
- npm 10+ (or compatible)

## 2) Environment

Create a local env file from the example:

```bash
cp .env.example .env.local
```

Default backend API base URL:

```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api/v1
NEXT_PUBLIC_ENABLE_QUERY_DEVTOOLS=false
```

## 3) Install dependencies

```bash
npm install
```

## 4) Run development server

```bash
npm run dev
```

Open: `http://localhost:3000`

## 5) Quality checks

```bash
npm run lint
npm run build
```

## 6) Current structure

- `src/app`: App Router pages and layouts
- `src/app/providers.tsx`: global provider composition (TanStack Query + toaster)
- `src/components`: shared UI and layout components
- `src/components/states`: standard loading, error, and empty state components
- `src/components/ui`: reusable UI primitives
- `src/features`: feature-oriented modules (next phases)
- `src/lib`: shared utilities
- `src/lib/query`: query client setup and defaults
- `src/services`: centralized API integration layer
- `src/hooks`: reusable hooks
- `src/types`: shared TypeScript models
- `src/config`: environment and app-level constants
- `src/styles`: global styles and Tailwind entrypoint
