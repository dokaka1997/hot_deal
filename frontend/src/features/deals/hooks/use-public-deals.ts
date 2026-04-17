"use client";

import { keepPreviousData, useQuery, type UseQueryOptions } from "@tanstack/react-query";

import { queryKeys } from "@/lib/query";
import { publicDealsService } from "@/services/public";
import type { DealListItem, PaginatedResult } from "@/types/domain";
import type { DealSearchRequest } from "@/types/requests";

type PublicDealsQueryOptions = Omit<
  UseQueryOptions<PaginatedResult<DealListItem>>,
  "queryKey" | "queryFn"
>;

export const usePublicDeals = (
  request: DealSearchRequest,
  options?: PublicDealsQueryOptions
) => {
  return useQuery({
    queryKey: queryKeys.publicDeals.list(request),
    queryFn: () => publicDealsService.getDeals(request),
    placeholderData: keepPreviousData,
    ...options
  });
};
