export interface AnalyticsDealCountBySource {
  sourceCode: string | null;
  sourceName: string | null;
  dealCount: number;
}

export interface AnalyticsDealCountByCategory {
  category: string | null;
  dealCount: number;
}

export interface AnalyticsHottestDeal {
  dealId: number;
  title: string;
  brand: string | null;
  category: string | null;
  sourceCode: string | null;
  sourceName: string | null;
  dealPrice: number;
  originalPrice: number | null;
  discountPercent: number | null;
  dealScore: number | null;
  lastSeenAt: Date | null;
  externalUrl: string | null;
  imageUrl: string | null;
}

export interface AnalyticsSummary {
  generatedAt: Date;
  activeOnly: boolean;
  activeDealCount: number;
  expiredDealCount: number;
  averageDiscountPercent: number | null;
  dealCountBySource: AnalyticsDealCountBySource[];
  dealCountByCategory: AnalyticsDealCountByCategory[];
  hottestDeals: AnalyticsHottestDeal[];
}
