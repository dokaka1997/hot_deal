"use client";

import { useQuery, type UseQueryOptions } from "@tanstack/react-query";

import { queryKeys } from "@/lib/query";
import { publicDealsService } from "@/services/public";
import type { DealDetail } from "@/types/domain";

type PublicDealDetailQueryOptions = Omit<
  UseQueryOptions<DealDetail>,
  "queryKey" | "queryFn"
>;

export const usePublicDealDetail = (
  dealId: number,
  options?: PublicDealDetailQueryOptions
) => {
  return useQuery({
    queryKey: queryKeys.publicDeals.detail(dealId),
    queryFn: () => publicDealsService.getDealDetail(dealId),
    enabled: dealId > 0,
    ...options
  });
};
