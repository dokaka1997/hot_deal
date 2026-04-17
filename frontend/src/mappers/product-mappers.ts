import { toRequiredDate } from "@/mappers/common-mappers";
import type { ProductDetailApi, ProductSummaryApi } from "@/types/contracts/backend";
import type { ProductDetail, ProductSummary } from "@/types/domain";

export const mapProductSummary = (api: ProductSummaryApi): ProductSummary => {
  return {
    id: api.id,
    name: api.name,
    brand: api.brand,
    category: api.category
  };
};

export const mapProductDetail = (api: ProductDetailApi): ProductDetail => {
  return {
    id: api.id,
    name: api.name,
    brand: api.brand,
    category: api.category,
    canonicalSku: api.canonicalSku,
    normalizedName: api.normalizedName,
    imageUrl: api.imageUrl,
    fingerprint: api.fingerprint,
    attributes: api.attributes,
    active: api.active,
    createdAt: toRequiredDate(api.createdAt),
    updatedAt: toRequiredDate(api.updatedAt)
  };
};
