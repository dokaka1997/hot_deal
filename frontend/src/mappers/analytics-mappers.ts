import { toDateOrNull, toNumberOrNull, toRequiredDate, toRequiredNumber } from "@/mappers/common-mappers";
import type {
  AnalyticsDealCountByCategoryApi,
  AnalyticsDealCountBySourceApi,
  AnalyticsHottestDealApi,
  AnalyticsSummaryApi
} from "@/types/contracts/backend";
import type {
  AnalyticsDealCountByCategory,
  AnalyticsDealCountBySource,
  AnalyticsHottestDeal,
  AnalyticsSummary
} from "@/types/domain";

export const mapAnalyticsDealCountBySource = (
  api: AnalyticsDealCountBySourceApi
): AnalyticsDealCountBySource => {
  return {
    sourceCode: api.sourceCode,
    sourceName: api.sourceName,
    dealCount: api.dealCount
  };
};

export const mapAnalyticsDealCountByCategory = (
  api: AnalyticsDealCountByCategoryApi
): AnalyticsDealCountByCategory => {
  return {
    category: api.category,
    dealCount: api.dealCount
  };
};

export const mapAnalyticsHottestDeal = (api: AnalyticsHottestDealApi): AnalyticsHottestDeal => {
  return {
    dealId: api.dealId,
    title: api.title,
    brand: api.brand,
    category: api.category,
    sourceCode: api.sourceCode,
    sourceName: api.sourceName,
    dealPrice: toRequiredNumber(api.dealPrice),
    originalPrice: toNumberOrNull(api.originalPrice),
    discountPercent: toNumberOrNull(api.discountPercent),
    dealScore: toNumberOrNull(api.dealScore),
    lastSeenAt: toDateOrNull(api.lastSeenAt),
    externalUrl: api.externalUrl,
    imageUrl: api.imageUrl
  };
};

export const mapAnalyticsSummary = (api: AnalyticsSummaryApi): AnalyticsSummary => {
  return {
    generatedAt: toRequiredDate(api.generatedAt),
    activeOnly: api.activeOnly,
    activeDealCount: api.activeDealCount,
    expiredDealCount: api.expiredDealCount,
    averageDiscountPercent: toNumberOrNull(api.averageDiscountPercent),
    dealCountBySource: api.dealCountBySource.map(mapAnalyticsDealCountBySource),
    dealCountByCategory: api.dealCountByCategory.map(mapAnalyticsDealCountByCategory),
    hottestDeals: api.hottestDeals.map(mapAnalyticsHottestDeal)
  };
};
