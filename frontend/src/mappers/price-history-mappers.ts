import { toRequiredDate, toRequiredNumber, toNumberOrNull } from "@/mappers/common-mappers";
import type { DealPriceHistoryApi, PriceHistoryPointApi } from "@/types/contracts/backend";
import type { DealPriceHistory, PriceHistoryPoint } from "@/types/domain";

export const mapPriceHistoryPoint = (api: PriceHistoryPointApi): PriceHistoryPoint => {
  return {
    id: api.id,
    dealId: api.dealId,
    productId: api.productId,
    sourceCode: api.sourceCode,
    sourceName: api.sourceName,
    capturedAt: toRequiredDate(api.capturedAt),
    currency: api.currency,
    originalPrice: toNumberOrNull(api.originalPrice),
    dealPrice: toRequiredNumber(api.dealPrice),
    discountPercent: toNumberOrNull(api.discountPercent),
    availabilityStatus: api.availabilityStatus
  };
};

export const mapDealPriceHistory = (api: DealPriceHistoryApi): DealPriceHistory => {
  return {
    dealId: api.dealId,
    points: api.points.map(mapPriceHistoryPoint)
  };
};
