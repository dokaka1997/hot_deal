import {
  mapDealDetail,
  mapDealListPage,
  mapDealPriceHistory,
  toDealPriceHistoryQuery,
  toDealSearchQuery
} from "@/mappers";
import { apiClient } from "@/services/api";
import type {
  DealDetailApi,
  DealListItemApi,
  DealPriceHistoryApi,
  PageResponseContract
} from "@/types/contracts/backend";
import type { DealDetail, DealListItem, DealPriceHistory, PaginatedResult } from "@/types/domain";
import type { DealPriceHistoryRequest, DealSearchRequest } from "@/types/requests";

const DEALS_ENDPOINT = "/deals";

export const publicDealsService = {
  async getDeals(request: DealSearchRequest): Promise<PaginatedResult<DealListItem>> {
    const data = await apiClient.get<PageResponseContract<DealListItemApi>>(DEALS_ENDPOINT, {
      query: toDealSearchQuery(request)
    });

    return mapDealListPage(data);
  },

  async getDealDetail(dealId: number): Promise<DealDetail> {
    const data = await apiClient.get<DealDetailApi>(`${DEALS_ENDPOINT}/${dealId}`);
    return mapDealDetail(data);
  },

  async getDealPriceHistory(request: DealPriceHistoryRequest): Promise<DealPriceHistory> {
    const data = await apiClient.get<DealPriceHistoryApi>(`${DEALS_ENDPOINT}/${request.dealId}/price-history`, {
      query: toDealPriceHistoryQuery(request)
    });

    return mapDealPriceHistory(data);
  }
};
