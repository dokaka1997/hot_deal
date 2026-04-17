"use client";

import { keepPreviousData, useQuery, type UseQueryOptions } from "@tanstack/react-query";

import { queryKeys } from "@/lib/query";
import { publicDealsService } from "@/services/public";
import type { DealPriceHistory } from "@/types/domain";
import type { DealPriceHistoryRequest } from "@/types/requests";

type PublicDealPriceHistoryQueryOptions = Omit<
  UseQueryOptions<DealPriceHistory>,
  "queryKey" | "queryFn"
>;

export const usePublicDealPriceHistory = (
  request: DealPriceHistoryRequest,
  options?: PublicDealPriceHistoryQueryOptions
) => {
  return useQuery({
    queryKey: queryKeys.publicDeals.priceHistory(request),
    queryFn: () => publicDealsService.getDealPriceHistory(request),
    enabled: request.dealId > 0,
    placeholderData: keepPreviousData,
    ...options
  });
};
