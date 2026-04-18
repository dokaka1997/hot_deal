import { mapProductSummary } from "@/mappers/product-mappers";
import { mapPageResponse, toDateOrNull, toNumberOrNull, toRequiredNumber } from "@/mappers/common-mappers";
import type { DealDetailApi, DealListItemApi, DealSourceApi, PageResponseContract } from "@/types/contracts/backend";
import type { DealDetail, DealListItem, DealSource, PaginatedResult } from "@/types/domain";

export const mapDealSource = (api: DealSourceApi): DealSource => {
  return {
    code: api.code,
    name: api.name,
    baseUrl: api.baseUrl
  };
};

export const mapDealListItem = (api: DealListItemApi): DealListItem => {
  return {
    id: api.id,
    title: api.title,
    normalizedTitle: api.normalizedTitle,
    description: api.description,
    brand: api.brand,
    category: api.category,
    imageUrl: api.imageUrl,
    externalUrl: api.externalUrl,
    couponCode: api.couponCode,
    currency: api.currency,
    originalPrice: toNumberOrNull(api.originalPrice),
    dealPrice: toRequiredNumber(api.dealPrice),
    discountPercent: toNumberOrNull(api.discountPercent),
    dealScore: toNumberOrNull(api.dealScore),
    status: api.status,
    validFrom: toDateOrNull(api.validFrom),
    validUntil: toDateOrNull(api.validUntil),
    firstSeenAt: toDateOrNull(api.firstSeenAt),
    lastSeenAt: toDateOrNull(api.lastSeenAt),
    source: mapDealSource(api.source),
    product: api.product ? mapProductSummary(api.product) : null
  };
};

export const mapDealDetail = (api: DealDetailApi): DealDetail => {
  return {
    id: api.id,
    title: api.title,
    normalizedTitle: api.normalizedTitle,
    description: api.description,
    brand: api.brand,
    category: api.category,
    imageUrl: api.imageUrl,
    externalUrl: api.externalUrl,
    couponCode: api.couponCode,
    currency: api.currency,
    originalPrice: toNumberOrNull(api.originalPrice),
    dealPrice: toRequiredNumber(api.dealPrice),
    discountPercent: toNumberOrNull(api.discountPercent),
    dealScore: toNumberOrNull(api.dealScore),
    status: api.status,
    validFrom: toDateOrNull(api.validFrom),
    validUntil: toDateOrNull(api.validUntil),
    firstSeenAt: toDateOrNull(api.firstSeenAt),
    lastSeenAt: toDateOrNull(api.lastSeenAt),
    source: mapDealSource(api.source),
    product: api.product ? mapProductSummary(api.product) : null
  };
};

export const mapDealListPage = (
  page: PageResponseContract<DealListItemApi>
): PaginatedResult<DealListItem> => mapPageResponse(page, mapDealListItem);
