import type { AnalyticsHottestDeal, DealListItem } from "@/types/domain";
import type { DealCardModel } from "@/features/deals/types";

export const toDealCardFromListItem = (deal: DealListItem): DealCardModel => {
  return {
    id: deal.id,
    title: deal.title,
    brand: deal.brand,
    category: deal.category,
    imageUrl: deal.imageUrl,
    externalUrl: deal.externalUrl,
    sourceName: deal.source.name,
    currency: deal.currency,
    dealPrice: deal.dealPrice,
    originalPrice: deal.originalPrice,
    discountPercent: deal.discountPercent,
    dealScore: deal.dealScore,
    lastSeenAt: deal.lastSeenAt
  };
};

export const toDealCardFromHottestDeal = (deal: AnalyticsHottestDeal): DealCardModel => {
  return {
    id: deal.dealId,
    title: deal.title,
    brand: deal.brand,
    category: deal.category,
    imageUrl: deal.imageUrl,
    externalUrl: deal.externalUrl,
    sourceName: deal.sourceName,
    currency: null,
    dealPrice: deal.dealPrice,
    originalPrice: deal.originalPrice,
    discountPercent: deal.discountPercent,
    dealScore: deal.dealScore,
    lastSeenAt: deal.lastSeenAt
  };
};
