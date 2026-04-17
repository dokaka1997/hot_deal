import type { DecimalValue, IsoDateTimeString } from "@/types/contracts/backend/common";
import type { DealStatus } from "@/types/contracts/backend/enums";
import type { ProductSummaryApi } from "@/types/contracts/backend/product";

export interface DealSourceApi {
  code: string | null;
  name: string | null;
}

export interface DealListItemApi {
  id: number;
  title: string;
  brand: string | null;
  category: string | null;
  imageUrl: string | null;
  externalUrl: string | null;
  currency: string;
  originalPrice: DecimalValue | null;
  dealPrice: DecimalValue;
  discountPercent: DecimalValue | null;
  dealScore: DecimalValue | null;
  status: DealStatus;
  validUntil: IsoDateTimeString | null;
  lastSeenAt: IsoDateTimeString | null;
  source: DealSourceApi;
}

export interface DealDetailApi {
  id: number;
  title: string;
  normalizedTitle: string | null;
  description: string | null;
  brand: string | null;
  category: string | null;
  imageUrl: string | null;
  externalUrl: string | null;
  couponCode: string | null;
  currency: string;
  originalPrice: DecimalValue | null;
  dealPrice: DecimalValue;
  discountPercent: DecimalValue | null;
  dealScore: DecimalValue | null;
  status: DealStatus;
  validFrom: IsoDateTimeString | null;
  validUntil: IsoDateTimeString | null;
  firstSeenAt: IsoDateTimeString | null;
  lastSeenAt: IsoDateTimeString | null;
  source: DealSourceApi;
  product: ProductSummaryApi | null;
}
