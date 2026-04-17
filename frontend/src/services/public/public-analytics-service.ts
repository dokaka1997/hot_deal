import { mapAnalyticsSummary, toAnalyticsSummaryQuery } from "@/mappers";
import { apiClient } from "@/services/api";
import type { AnalyticsSummaryApi } from "@/types/contracts/backend";
import type { AnalyticsSummary } from "@/types/domain";
import type { AnalyticsSummaryRequest } from "@/types/requests";

const ANALYTICS_SUMMARY_ENDPOINT = "/analytics/summary";

export const publicAnalyticsService = {
  async getSummary(request: AnalyticsSummaryRequest = {}): Promise<AnalyticsSummary> {
    const data = await apiClient.get<AnalyticsSummaryApi>(ANALYTICS_SUMMARY_ENDPOINT, {
      query: toAnalyticsSummaryQuery(request)
    });

    return mapAnalyticsSummary(data);
  }
};
