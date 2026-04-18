import type { DealStatus } from "@/types/contracts/backend";
import type { ProductSummary } from "@/types/domain/product";

export interface DealSource {
  code: string | null;
  name: string | null;
  baseUrl: string | null;
}

export interface DealListItem {
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
  originalPrice: number | null;
  dealPrice: number;
  discountPercent: number | null;
  dealScore: number | null;
  status: DealStatus;
  validFrom: Date | null;
  validUntil: Date | null;
  firstSeenAt: Date | null;
  lastSeenAt: Date | null;
  source: DealSource;
  product: ProductSummary | null;
}

export interface DealDetail extends DealListItem {
  normalizedTitle: string | null;
}
