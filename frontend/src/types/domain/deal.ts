import type { DealStatus } from "@/types/contracts/backend";
import type { ProductSummary } from "@/types/domain/product";

export interface DealSource {
  code: string | null;
  name: string | null;
}

export interface DealListItem {
  id: number;
  title: string;
  brand: string | null;
  category: string | null;
  imageUrl: string | null;
  externalUrl: string | null;
  currency: string;
  originalPrice: number | null;
  dealPrice: number;
  discountPercent: number | null;
  dealScore: number | null;
  status: DealStatus;
  validUntil: Date | null;
  lastSeenAt: Date | null;
  source: DealSource;
}

export interface DealDetail extends DealListItem {
  normalizedTitle: string | null;
  description: string | null;
  couponCode: string | null;
  validFrom: Date | null;
  firstSeenAt: Date | null;
  product: ProductSummary | null;
}
