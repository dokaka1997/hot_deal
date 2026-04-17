"use client";

import { useQuery } from "@tanstack/react-query";
import type { UseQueryOptions } from "@tanstack/react-query";

import { queryKeys } from "@/lib/query";
import { publicAnalyticsService } from "@/services/public";
import type { AnalyticsSummary } from "@/types/domain";
import type { AnalyticsSummaryRequest } from "@/types/requests";

type AnalyticsSummaryQueryOptions = Omit<
  UseQueryOptions<AnalyticsSummary>,
  "queryKey" | "queryFn"
>;

export const usePublicAnalyticsSummary = (
  request: AnalyticsSummaryRequest,
  options?: AnalyticsSummaryQueryOptions
) => {
  return useQuery({
    queryKey: queryKeys.publicAnalytics.summary(request),
    queryFn: () => publicAnalyticsService.getSummary(request),
    ...options
  });
};
