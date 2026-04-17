import type {
  AnalyticsSummaryRequest,
  DealPriceHistoryRequest,
  DealSearchRequest
} from "@/types/requests";

export const queryKeys = {
  publicDeals: {
    list: (request: DealSearchRequest) => ["public", "deals", "list", request] as const,
    detail: (dealId: number) => ["public", "deals", "detail", dealId] as const,
    priceHistory: (request: DealPriceHistoryRequest) =>
      ["public", "deals", "price-history", request] as const
  },
  publicAnalytics: {
    summary: (request: AnalyticsSummaryRequest) => ["public", "analytics", "summary", request] as const
  }
} as const;
