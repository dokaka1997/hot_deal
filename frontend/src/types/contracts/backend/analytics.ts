import type { DecimalValue, IsoDateTimeString } from "@/types/contracts/backend/common";

export interface AnalyticsDealCountBySourceApi {
  sourceCode: string | null;
  sourceName: string | null;
  dealCount: number;
}

export interface AnalyticsDealCountByCategoryApi {
  category: string | null;
  dealCount: number;
}

export interface AnalyticsHottestDealApi {
  dealId: number;
  title: string;
  brand: string | null;
  category: string | null;
  sourceCode: string | null;
  sourceName: string | null;
  dealPrice: DecimalValue;
  originalPrice: DecimalValue | null;
  discountPercent: DecimalValue | null;
  dealScore: DecimalValue | null;
  lastSeenAt: IsoDateTimeString | null;
  externalUrl: string | null;
  imageUrl: string | null;
}

export interface AnalyticsSummaryApi {
  generatedAt: IsoDateTimeString;
  activeOnly: boolean;
  activeDealCount: number;
  expiredDealCount: number;
  averageDiscountPercent: DecimalValue | null;
  dealCountBySource: AnalyticsDealCountBySourceApi[];
  dealCountByCategory: AnalyticsDealCountByCategoryApi[];
  hottestDeals: AnalyticsHottestDealApi[];
}
